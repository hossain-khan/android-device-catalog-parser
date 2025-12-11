package dev.hossain.example

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import java.io.File

/**
 * Handles database operations for storing and retrieving Android devices.
 */
class DatabaseOperations {
    /**
     * Processes device records into a SQLite database.
     *
     * @param devices List of AndroidDevice objects to store
     * @param dbPath Path to the database file (defaults to "devices.db")
     * @return true if operation was successful
     */
    fun processRecordsToDb(
        devices: List<AndroidDevice>,
        dbPath: String = "devices.db",
    ): Boolean {
        try {
            // Create or recreate database file
            val dbFile = File(dbPath)
            if (dbFile.exists()) {
                dbFile.delete()
            }

            // Initialize database
            val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
            val database = DeviceDatabase(driver)
            DeviceDatabase.Schema.create(driver)

            val deviceQueries = database.deviceQueries

            // Insert all devices
            devices.forEachIndexed { index, androidDevice ->
                deviceQueries.transaction {
                    deviceQueries.insert(
                        brand = androidDevice.brand,
                        device = androidDevice.device,
                        manufacturer = androidDevice.manufacturer,
                        model_name = androidDevice.modelName,
                        ram = androidDevice.ram,
                        form_factor = androidDevice.formFactor.value,
                        processor_name = androidDevice.processorName,
                        gpu = androidDevice.gpu,
                    )

                    val deviceId: Long = deviceQueries.lastInsertRowId().executeAsOne()

                    // Insert related data
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
            }

            println("✓ Inserted ${devices.size} records to database: $dbPath")
            return true
        } catch (e: Exception) {
            System.err.println("✗ Database operation failed: ${e.message}")
            return false
        }
    }

    /**
     * Retrieves all devices from the database and converts them to AndroidDevice objects.
     *
     * @param dbPath Path to the database file
     * @return List of AndroidDevice objects from the database
     */
    fun retrieveDevicesFromDb(dbPath: String = "devices.db"): List<AndroidDevice> {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
        val database = DeviceDatabase(driver)
        val deviceQueries = database.deviceQueries

        val deviceListDb: List<Device> = deviceQueries.selectAll().executeAsList()

        return deviceListDb.map { dbDevice ->
            AndroidDevice(
                brand = dbDevice.brand,
                device = dbDevice.device,
                manufacturer = dbDevice.manufacturer,
                modelName = dbDevice.model_name,
                ram = dbDevice.ram,
                formFactor = FormFactor.fromValue(dbDevice.form_factor),
                processorName = dbDevice.processor_name,
                gpu = dbDevice.gpu,
                screenSizes = deviceQueries.getScreenSize(dbDevice._id).executeAsList().map { it.screen_size },
                screenDensities =
                    deviceQueries
                        .getScreenDensity(dbDevice._id)
                        .executeAsList()
                        .map { it.screen_density.toInt() },
                abis = deviceQueries.getAbi(dbDevice._id).executeAsList().map { it.abi },
                sdkVersions = deviceQueries.getSdkVersion(dbDevice._id).executeAsList().map { it.sdk_version.toInt() },
                openGlEsVersions =
                    deviceQueries
                        .getOpenGlVersion(dbDevice._id)
                        .executeAsList()
                        .map { it.opengl_version },
            )
        }
    }

    /**
     * Validates that devices stored in the database match the source data.
     *
     * @param sourceDevices Original list of devices
     * @param dbPath Path to the database file
     * @return true if devices match
     */
    fun validateDatabaseIntegrity(
        sourceDevices: List<AndroidDevice>,
        dbPath: String = "devices.db",
    ): Boolean {
        val devicesFromDb = retrieveDevicesFromDb(dbPath)
        val isMatch = sourceDevices == devicesFromDb

        if (isMatch) {
            println("✓ Database integrity validated: All ${devicesFromDb.size} devices match source data")
        } else {
            System.err.println("✗ Database integrity check failed: Data mismatch")
        }

        return isMatch
    }
}
