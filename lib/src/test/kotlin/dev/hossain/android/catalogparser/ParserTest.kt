package dev.hossain.android.catalogparser

import com.google.gson.Gson
import dev.hossain.android.catalogparser.models.AndroidDevice
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * Tests the Android device catalog CSV parser.
 */
class ParserTest {
    private lateinit var sut: Parser

    @BeforeTest
    fun setup() {
        sut = Parser()
    }

    @Test
    fun `given no csv header fails to parse devices`() {
        val csvData: String = """
        10.or,D,10or_D,2868-2874MB,Phone,Qualcomm MSM8917,720x1280,320,arm64-v8a;armeabi;armeabi-v7a,25;27,3.0
        10.or,E,E,1857-2846MB,Phone,Qualcomm MSM8937,1080x1920,480,arm64-v8a;armeabi;armeabi-v7a,25;27,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(0, parsedDevices.size)
    }

    @Test
    fun `given valid csv data parses device list`() {
        val csvData: String = """
        Manufacturer,Model Name,Model Code,RAM (TotalMem),Form Factor,System on Chip,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        10.or,D,10or_D,2868-2874MB,Phone,Qualcomm MSM8917,720x1280,320,arm64-v8a;armeabi;armeabi-v7a,25;27,3.0
        10.or,E,E,1857-2846MB,Phone,Qualcomm MSM8937,1080x1920,480,arm64-v8a;armeabi;armeabi-v7a,25;27,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(2, parsedDevices.size)
    }

    @Test
    fun `given valid csv data row parses device attributes`() {
        val csvData: String = """
        Manufacturer,Model Name,Model Code,RAM (TotalMem),Form Factor,System on Chip,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        Google,Pixel 4 XL,coral,5466MB,Phone,Qualcomm SDM855,1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,29;30,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)
        val device = parsedDevices.first()

        assertEquals("Google", device.manufacturer)
        assertEquals("Pixel 4 XL", device.modelName)
        assertEquals("coral", device.modelCode)
        assertEquals("5466MB", device.ram)
        assertEquals("Phone", device.formFactor)
        assertEquals("Qualcomm SDM855", device.processorName)
        assertEquals(listOf("1440x3040"), device.screenSizes)
        assertEquals(listOf(560), device.screenDensities)
        assertEquals(listOf("arm64-v8a", "armeabi", "armeabi-v7a"), device.abis)
        assertEquals(listOf(29, 30), device.sdkVersions)
        assertEquals(listOf("3.2"), device.openGlEsVersions)
    }

    @Test
    fun `given all catalog data is loaded then parsers all devices`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv").bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = sut.parseDeviceCatalogData(csvFileContent)

        assertTrue(parsedDevices.size > 1000, "Total parsed devices should be more than thousand")
    }

    @Test
    fun `given parsed devices available - can converts to JSON`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv").bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = sut.parseDeviceCatalogData(csvFileContent)

        val gson = Gson()
        val convertedJson = gson.toJson(parsedDevices)

        assertTrue(convertedJson.length > 1_000_000)

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // Use following to write file to resources
        /*val resourceDirectory: Path = Paths.get("src", "test", "resources")
        val resDir = resourceDirectory.toFile()
        assertTrue(resDir.absolutePath.endsWith("src/test/resources"))

        val jsonFile: File = File(resDir, "android-devices-catalog-min.json")
        jsonFile.writeText(convertedJson)*/
    }
}
