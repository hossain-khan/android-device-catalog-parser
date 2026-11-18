package dev.hossain.example

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [DeviceMetadataCalculator].
 */
class DeviceMetadataCalculatorTest {
    @Test
    fun `parseRamMb handles single, range, GB, whitespace, and lowercase formats`() {
        assertEquals(8000 to 8000, DeviceMetadataCalculator.parseRamMb("8000MB"))
        assertEquals(12212 to 16415, DeviceMetadataCalculator.parseRamMb("12212-16415MB"))
        assertEquals(12212 to 16415, DeviceMetadataCalculator.parseRamMb("16415-12212MB"))
        assertEquals(4096 to 4096, DeviceMetadataCalculator.parseRamMb("4GB"))
        assertEquals(4096 to 8192, DeviceMetadataCalculator.parseRamMb("4-8GB"))
        assertEquals(8000 to 8000, DeviceMetadataCalculator.parseRamMb("  8000 mb  "))
        assertEquals(4096 to 8192, DeviceMetadataCalculator.parseRamMb(" 4 - 8 gb "))
    }

    @Test
    fun `parseRamMb returns zero pair for invalid or missing RAM strings`() {
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb(""))
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb("Unknown"))
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb("8000"))
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb("MB"))
        assertEquals(0 to 0, DeviceMetadataCalculator.parseRamMb("-100MB"))
    }

    @Test
    fun `computeSortScore prioritizes maxSdk over all lower-tier attributes`() {
        val newerLowEndTablet =
            createDevice(
                manufacturer = "Generic",
                formFactor = FormFactor.TABLET,
                ram = "2000MB",
                sdkVersions = listOf(28, 36),
            )
        val olderFlagshipPhone =
            createDevice(
                manufacturer = "Google",
                formFactor = FormFactor.PHONE,
                ram = "16000MB",
                sdkVersions = listOf(35, 35),
            )

        val newerScore = DeviceMetadataCalculator.computeSortScore(newerLowEndTablet, 28, 36, 2000)
        val olderScore = DeviceMetadataCalculator.computeSortScore(olderFlagshipPhone, 35, 35, 16000)

        assertTrue(newerScore > olderScore, "Higher maxSdk ($newerScore) must beat lower maxSdk ($olderScore)")
    }

    @Test
    fun `computeSortScore prioritizes PHONE form factor over non-phone at equal maxSdk`() {
        val phone = createDevice(manufacturer = "Generic", formFactor = FormFactor.PHONE, ram = "2000MB")
        val tablet = createDevice(manufacturer = "Google", formFactor = FormFactor.TABLET, ram = "16000MB")

        val phoneScore = DeviceMetadataCalculator.computeSortScore(phone, minSdk = 21, maxSdk = 35, ramMaxMb = 2000)
        val tabletScore = DeviceMetadataCalculator.computeSortScore(tablet, minSdk = 35, maxSdk = 35, ramMaxMb = 16000)

        assertTrue(phoneScore > tabletScore, "Phone ($phoneScore) must beat non-phone ($tabletScore) at equal maxSdk")
    }

    @Test
    fun `computeSortScore prioritizes minSdk over RAM tier and OEM at equal maxSdk and form factor`() {
        val newerLaunch = createDevice(manufacturer = "Generic", formFactor = FormFactor.PHONE)
        val olderLaunch = createDevice(manufacturer = "Samsung", formFactor = FormFactor.PHONE)

        val newerScore = DeviceMetadataCalculator.computeSortScore(newerLaunch, minSdk = 34, maxSdk = 35, ramMaxMb = 2000)
        val olderScore = DeviceMetadataCalculator.computeSortScore(olderLaunch, minSdk = 33, maxSdk = 35, ramMaxMb = 16000)

        assertTrue(newerScore > olderScore, "Higher minSdk ($newerScore) must beat lower minSdk ($olderScore)")
    }

    @Test
    fun `computeSortScore applies exact RAM tier boundaries`() {
        val base = createDevice(manufacturer = "Generic", formFactor = FormFactor.PHONE)

        val below4k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 3999)
        val at4k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 4000)
        val below6k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 5999)
        val at6k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 6000)
        val below8k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 7999)
        val at8k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 8000)
        val below12k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 11999)
        val at12k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 12000)
        val below16k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 15999)
        val at16k = DeviceMetadataCalculator.computeSortScore(base, 34, 35, 16000)

        assertEquals(below4k + 10, at4k)
        assertEquals(at4k, below6k)
        assertEquals(below6k + 10, at6k)
        assertEquals(at6k, below8k)
        assertEquals(below8k + 10, at8k)
        assertEquals(at8k, below12k)
        assertEquals(below12k + 10, at12k)
        assertEquals(at12k, below16k)
        assertEquals(below16k + 10, at16k)
    }

    @Test
    fun `computeSortScore applies major OEM tie-breaker case-insensitively`() {
        val majorOem = createDevice(manufacturer = "  SaMsUnG  ", formFactor = FormFactor.PHONE)
        val otherOem = createDevice(manufacturer = "Generic", formFactor = FormFactor.PHONE)

        val majorScore = DeviceMetadataCalculator.computeSortScore(majorOem, 34, 35, 8000)
        val otherScore = DeviceMetadataCalculator.computeSortScore(otherOem, 34, 35, 8000)

        assertEquals(otherScore + 5, majorScore)
    }

    private fun createDevice(
        manufacturer: String = "Google",
        formFactor: FormFactor = FormFactor.PHONE,
        ram: String = "8000MB",
        sdkVersions: List<Int> = listOf(34, 35),
    ): AndroidDevice =
        AndroidDevice(
            brand = manufacturer.lowercase(),
            device = "test_codename",
            manufacturer = manufacturer,
            modelName = "Test Model",
            ram = ram,
            formFactor = formFactor,
            processorName = "Test SoC",
            gpu = "Test GPU",
            screenSizes = listOf("1080x2400"),
            screenDensities = listOf(420),
            abis = listOf("arm64-v8a"),
            sdkVersions = sdkVersions,
            openGlEsVersions = listOf("3.2"),
        )
}
