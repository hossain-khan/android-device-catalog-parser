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

    processRecordsToDb(parsedDevices.take(100))
}

private fun processRecordsToDb(parsedDevices: List<AndroidDevice>) {
    val dbFile = File("devices.db")
    if(dbFile.exists()) {
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
                processor_name = androidDevice.processorName,
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
    println("Inserted ${deviceQueries.selectAll().executeAsList().size} records to DB")
}