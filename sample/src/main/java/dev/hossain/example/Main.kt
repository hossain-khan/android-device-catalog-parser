package dev.hossain.example

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.AndroidDevice
import java.io.File
import java.util.Date

fun main() {
    println("Application run on ${Date()}")

    val parser = Parser()
    val csvFileContent =
        object {}.javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()
    val parsedDevices: List<AndroidDevice> = parser.parseDeviceCatalogData(csvFileContent)

    println("Parsed ${parsedDevices.size} devices from the catalog CSV file.")

    processRecordsToDb(parsedDevices)
}

private fun processRecordsToDb(parsedDevices: List<AndroidDevice>) {
    val dbFile = File("devices.db")
    if (dbFile.exists()) {
        dbFile.delete()
    }
    val driver: SqlDriver = JdbcSqliteDriver("${JdbcSqliteDriver.IN_MEMORY}devices.db")

    val database = DeviceDatabase(driver)
    DeviceDatabase.Schema.create(driver)

    val deviceQueries = database.deviceQueries

    parsedDevices.forEach { androidDevice ->
        deviceQueries.transaction {
            deviceQueries.insert(
                brand = androidDevice.brand,
                device = androidDevice.device,
                manufacturer = androidDevice.manufacturer,
                model_name = androidDevice.modelName,
                ram = androidDevice.ram,
                form_factor = androidDevice.formFactor,
                processor_name = androidDevice.processorName,
                gpu = androidDevice.gpu
            )
        }

        val deviceId: Long = deviceQueries.lastInsertRowId().executeAsOne()
        println("Inserted new record with id: $deviceId")

        androidDevice.abis.forEach {
            deviceQueries.insertAbi(deviceId, it)
        }
        androidDevice.openGlEsVersions.forEach {
            deviceQueries.insertOpenGlVersion(deviceId, it)
        }
        androidDevice.screenDensities.forEach {
            deviceQueries.insertScreenDensity(deviceId, it.toLong())
        }
        androidDevice.screenSizes.forEach {
            deviceQueries.insertScreenSize(deviceId, it)
        }
        androidDevice.sdkVersions.forEach {
            deviceQueries.insertSdkVersion(deviceId, it.toLong())
        }
    }
    val deviceListDb: List<Device> = deviceQueries.selectAll().executeAsList()
    println("Inserted ${deviceListDb.size} records to DB")

    val androidDeviceBack: List<AndroidDevice> = deviceListDb.map { dbDevice ->
        AndroidDevice(
            brand = dbDevice.brand,
            device = dbDevice.device,
            manufacturer = dbDevice.manufacturer,
            modelName = dbDevice.model_name,
            ram = dbDevice.ram,
            formFactor = dbDevice.form_factor,
            processorName = dbDevice.processor_name,
            gpu = dbDevice.gpu,
            screenSizes = deviceQueries.getScreenSize(dbDevice._id).executeAsList().map { it.screen_size },
            screenDensities = deviceQueries.getScreenDensity(dbDevice._id).executeAsList()
                .map { it.screen_density.toInt() },
            abis = deviceQueries.getAbi(dbDevice._id).executeAsList().map { it.abi },
            sdkVersions = deviceQueries.getSdkVersion(dbDevice._id).executeAsList().map { it.sdk_version.toInt() },
            openGlEsVersions = deviceQueries.getOpenGlVersion(dbDevice._id).executeAsList().map { it.opengl_version },
        )
    }

    println("Got all the Android device model back. Total: ${androidDeviceBack.size}")
    println("Are they same as source? ${parsedDevices == androidDeviceBack}")
}