package dev.hossain.android.catalogparser.models

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [ParseResult] data class.
 */
class ParseResultTest {

    private fun createDevice(modelName: String): AndroidDevice {
        return AndroidDevice(
            brand = "Brand",
            device = "Device",
            manufacturer = "Manufacturer",
            modelName = modelName,
            ram = "2GB",
            formFactor = FormFactor.PHONE,
            processorName = "Snapdragon",
            gpu = "Adreno",
            screenSizes = listOf("1080x1920"),
            screenDensities = listOf(480),
            abis = listOf("arm64-v8a"),
            sdkVersions = listOf(28),
            openGlEsVersions = listOf("3.2")
        )
    }

    @Test
    fun `successfulCount should return correct number of devices`() {
        val devices = listOf(createDevice("Pixel 4"), createDevice("Pixel 5"))
        val parseResult = ParseResult(
            devices = devices,
            totalRows = 5,
            discardedCount = 3,
            discardReasons = mapOf("Reason 1" to 2, "Reason 2" to 1)
        )
        assertEquals(2, parseResult.successfulCount)
    }

    @Test
    fun `successfulCount should return zero when no devices are parsed`() {
        val parseResult = ParseResult(
            devices = emptyList(),
            totalRows = 5,
            discardedCount = 5,
            discardReasons = mapOf("Reason 1" to 5)
        )
        assertEquals(0, parseResult.successfulCount)
    }

    @Test
    fun `successRate should be zero when totalRows is zero`() {
        val parseResult = ParseResult(
            devices = emptyList(),
            totalRows = 0,
            discardedCount = 0,
            discardReasons = emptyMap()
        )
        assertEquals(0.0, parseResult.successRate)
    }

    @Test
    fun `successRate should be calculated correctly for partial success`() {
        val devices = listOf(createDevice("Pixel 4"), createDevice("Pixel 5"))
        val parseResult = ParseResult(
            devices = devices,
            totalRows = 4,
            discardedCount = 2,
            discardReasons = mapOf("Reason 1" to 2)
        )
        assertEquals(50.0, parseResult.successRate)
    }

    @Test
    fun `successRate should be 100 when all rows are parsed successfully`() {
        val devices = listOf(createDevice("Pixel 4"), createDevice("Pixel 5"))
        val parseResult = ParseResult(
            devices = devices,
            totalRows = 2,
            discardedCount = 0,
            discardReasons = emptyMap()
        )
        assertEquals(100.0, parseResult.successRate)
    }

    @Test
    fun `successRate should be zero when no rows are parsed successfully`() {
        val parseResult = ParseResult(
            devices = emptyList(),
            totalRows = 5,
            discardedCount = 5,
            discardReasons = mapOf("Reason 1" to 5)
        )
        assertEquals(0.0, parseResult.successRate)
    }
}
