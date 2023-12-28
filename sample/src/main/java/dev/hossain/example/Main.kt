package dev.hossain.example

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.AndroidDevice
import java.io.File
import java.util.Date

/**
 * The main function of the application.
 * It reads the Android devices catalog CSV file, parses it into a list of AndroidDevice objects,
 * and then processes these records into a SQLite database.
 */
fun main() {
    // Print the current date and time to the console.
    println("Application run on ${Date()}")

    // Create a new instance of the Parser class.
    val parser = Parser()

    // Read the Android devices catalog CSV file from the resources directory.
    val csvFileContent =
        object {}.javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()

    // Parse the CSV file content into a list of AndroidDevice objects.
    val parsedDevices: List<AndroidDevice> = parser.parseDeviceCatalogData(csvFileContent)

    // Print the number of parsed devices to the console.
    println("Parsed ${parsedDevices.size} devices from the catalog CSV file.")

    // Process the parsed devices into a SQLite database.
    processRecordsToDb(parsedDevices)
}

/**
 * Processes the parsed Android devices into a SQLite database.
 *
 * @param parsedDevices The list of AndroidDevice objects parsed from the CSV file.
 */
private fun processRecordsToDb(parsedDevices: List<AndroidDevice>) {
    // Create a new File object for the SQLite database.
    val dbFile = File("devices.db")

    // If the database file already exists, delete it.
    if (dbFile.exists()) {
        dbFile.delete()
    }

    // Create a new SQLite driver for the database.
    val driver: SqlDriver = JdbcSqliteDriver("${JdbcSqliteDriver.IN_MEMORY}devices.db")

    // Create a new DeviceDatabase object with the SQLite driver.
    val database = DeviceDatabase(driver)

    // Create the schema for the DeviceDatabase.
    DeviceDatabase.Schema.create(driver)

    // Get the DeviceQueries object from the DeviceDatabase.
    val deviceQueries = database.deviceQueries

    parsedDevices.forEach { androidDevice ->
        deviceQueries.transaction {
            // Insert the AndroidDevice into the database.
            deviceQueries.insert(
                brand = androidDevice.brand,
                device = androidDevice.device,
                manufacturer = androidDevice.manufacturer,
                model_name = androidDevice.modelName,
                ram = androidDevice.ram,
                form_factor = androidDevice.formFactor,
                processor_name = androidDevice.processorName,
                gpu = androidDevice.gpu,
            )
        }

        // Get the ID of the last inserted row.
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

    // Get all the Device records from the database.
    val deviceListDb: List<Device> = deviceQueries.selectAll().executeAsList()

    // Print the number of records inserted into the database to the console.
    println("Inserted ${deviceListDb.size} records to DB")

    // Map the Device records from the database to AndroidDevice objects.
    val androidDevicesFromDb: List<AndroidDevice> =
        deviceListDb.map { dbDevice ->
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
                screenDensities =
                    deviceQueries.getScreenDensity(dbDevice._id).executeAsList()
                        .map { it.screen_density.toInt() },
                abis = deviceQueries.getAbi(dbDevice._id).executeAsList().map { it.abi },
                sdkVersions = deviceQueries.getSdkVersion(dbDevice._id).executeAsList().map { it.sdk_version.toInt() },
                openGlEsVersions = deviceQueries.getOpenGlVersion(dbDevice._id).executeAsList().map { it.opengl_version },
            )
        }

    // Print the total number of AndroidDevice objects retrieved from the database to the console.
    println("Got all the Android device model back. Total: ${androidDevicesFromDb.size}")

    // Print whether the AndroidDevice objects retrieved from the database are the same as the parsed AndroidDevice objects.
    println("Are they same as source? ${parsedDevices == androidDevicesFromDb}")
}
