package dev.hossain.example

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test cases to verify the database schema constraints, precomputed columns, and FTS search work correctly.
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
            min_sdk = 30,
            max_sdk = 34,
            ram_min_mb = 4096,
            ram_max_mb = 4096,
            sort_score = 3413015,
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
            min_sdk = 31,
            max_sdk = 35,
            ram_min_mb = 8192,
            ram_max_mb = 8192,
            sort_score = 3503130,
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
                "Desktop",
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
                min_sdk = 30,
                max_sdk = 34,
                ram_min_mb = (index + 1).toLong() * 1024,
                ram_max_mb = (index + 1).toLong() * 1024,
                sort_score = 3403000,
            )
        }

        // Verify all records were inserted successfully
        val devices = deviceQueries.selectAll().executeAsList()
        assertTrue(devices.size == validFormFactors.size)

        // Verify all form factors match what we inserted
        val insertedFormFactors = devices.map { it.form_factor }.toSet()
        assertTrue(insertedFormFactors == validFormFactors.toSet())
    }

    @Test
    fun `parseRamMb parses single, range, GB, and unknown RAM formats`() {
        assertEquals(8000 to 8000, DeviceMetadataCalculator.parseRamMb("8000MB"))
        assertEquals(12212 to 16415, DeviceMetadataCalculator.parseRamMb("12212-16415MB"))
        assertEquals(4096 to 4096, DeviceMetadataCalculator.parseRamMb("4GB"))
        assertEquals(4096 to 8192, DeviceMetadataCalculator.parseRamMb("4-8GB"))
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb("Unknown"))
    }

    @Test
    fun `selectBySortScore and searchFts return expected devices`() {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val database = DeviceDatabase(driver)
        DeviceDatabase.Schema.create(driver)

        val phone =
            AndroidDevice(
                brand = "google",
                device = "husky",
                manufacturer = "Google",
                modelName = "Pixel 8 Pro",
                ram = "12212MB",
                formFactor = FormFactor.PHONE,
                processorName = "Tensor G3",
                gpu = "Mali-G715",
                screenSizes = listOf("1344x2992"),
                screenDensities = listOf(480),
                abis = listOf("arm64-v8a"),
                sdkVersions = listOf(34, 35),
                openGlEsVersions = listOf("3.2"),
            )
        val tablet =
            AndroidDevice(
                brand = "google",
                device = "tangorpro",
                manufacturer = "Google",
                modelName = "Pixel Tablet",
                ram = "8000MB",
                formFactor = FormFactor.TABLET,
                processorName = "Tensor G2",
                gpu = "Mali-G710",
                screenSizes = listOf("2560x1600"),
                screenDensities = listOf(320),
                abis = listOf("arm64-v8a"),
                sdkVersions = listOf(33, 35),
                openGlEsVersions = listOf("3.2"),
            )

        val deviceQueries = database.deviceQueries
        listOf(tablet, phone).forEach { d ->
            val minSdk = d.sdkVersions.minOrNull() ?: 0
            val maxSdk = d.sdkVersions.maxOrNull() ?: 0
            val (ramMin, ramMax) = DeviceMetadataCalculator.parseRamMb(d.ram)
            val score = DeviceMetadataCalculator.computeSortScore(d, minSdk, maxSdk, ramMax)
            deviceQueries.insert(
                brand = d.brand,
                device = d.device,
                manufacturer = d.manufacturer,
                model_name = d.modelName,
                ram = d.ram,
                form_factor = d.formFactor.value,
                processor_name = d.processorName,
                gpu = d.gpu,
                min_sdk = minSdk.toLong(),
                max_sdk = maxSdk.toLong(),
                ram_min_mb = ramMin.toLong(),
                ram_max_mb = ramMax.toLong(),
                sort_score = score.toLong(),
            )
        }
        driver.execute(null, "INSERT INTO device_fts(device_fts) VALUES ('rebuild')", 0)

        val sorted = deviceQueries.selectBySortScore(10, 0).executeAsList()
        assertEquals(listOf("Pixel 8 Pro", "Pixel Tablet"), sorted.map { it.model_name })
        assertEquals(34L, sorted[0].min_sdk)
        assertEquals(35L, sorted[0].max_sdk)
        assertEquals(12212L, sorted[0].ram_min_mb)
        assertEquals(12212L, sorted[0].ram_max_mb)

        val ftsMatches = deviceQueries.searchFts("Pixel Pro").executeAsList()
        assertEquals(listOf("Pixel 8 Pro"), ftsMatches.map { it.model_name })
    }
}
