package dev.hossain.example

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test cases to verify the database schema constraints work correctly
 */
class DatabaseSchemaTest {
    @Test
    fun `database schema allows inserting device records`() {
        // Create an in-memory SQLite database for testing
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val database = DeviceDatabase(driver)
        DeviceDatabase.Schema.create(driver)

        val deviceQueries = database.deviceQueries

        // Test that valid form factor values work
        deviceQueries.insert(
            brand = "Test",
            device = "test_device",
            manufacturer = "Test Manufacturer",
            model_name = "Test Model",
            ram = "4GB",
            form_factor = "Phone",
            processor_name = "Test Processor",
            gpu = "Test GPU",
        )

        // Verify the record was inserted
        val devices = deviceQueries.selectAll().executeAsList()
        assertTrue(devices.size == 1)

        // Test that additional records can be inserted
        deviceQueries.insert(
            brand = "Test2",
            device = "test_device2",
            manufacturer = "Test Manufacturer2",
            model_name = "Test Model2",
            ram = "8GB",
            form_factor = "Tablet",
            processor_name = "Test Processor2",
            gpu = "Test GPU2",
        )

        // Verify both records exist in the database
        val devicesAfterInsert = deviceQueries.selectAll().executeAsList()
        assertTrue(devicesAfterInsert.size == 2)
    }

    @Test
    fun `database accepts all valid FormFactor enum values`() {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val database = DeviceDatabase(driver)
        DeviceDatabase.Schema.create(driver)

        val deviceQueries = database.deviceQueries

        // Test all valid form factor values from FormFactor enum
        val validFormFactors =
            listOf(
                "Phone",
                "Tablet",
                "TV",
                "Wearable",
                "Android Automotive",
                "Chromebook",
                "Google Play Games on PC",
            )

        validFormFactors.forEachIndexed { index, formFactor ->
            deviceQueries.insert(
                brand = "Test$index",
                device = "test_device$index",
                manufacturer = "Test Manufacturer$index",
                model_name = "Test Model$index",
                ram = "${index + 1}GB",
                form_factor = formFactor,
                processor_name = "Test Processor$index",
                gpu = "Test GPU$index",
            )
        }

        // Verify all records were inserted successfully
        val devices = deviceQueries.selectAll().executeAsList()
        assertTrue(devices.size == validFormFactors.size)

        // Verify all form factors match what we inserted
        val insertedFormFactors = devices.map { it.form_factor }.toSet()
        assertTrue(insertedFormFactors == validFormFactors.toSet())
    }
}
