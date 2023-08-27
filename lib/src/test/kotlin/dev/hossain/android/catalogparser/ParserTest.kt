package dev.hossain.android.catalogparser

import com.google.gson.Gson
import dev.hossain.android.catalogparser.models.AndroidDevice
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
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
        motorola,ali,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,26;28,3.2
        motorola,ali_n,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,28,3.2
        motorola,aljeter,Motorola,moto g(6) play,3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(0, parsedDevices.size)
    }

    @Test
    fun `given valid csv data parses device list`() {
        val csvData: String = """
        Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        motorola,ali,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,26;28,3.2
        motorola,ali_n,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,28,3.2
        motorola,aljeter,Motorola,moto g(6) play,3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(3, parsedDevices.size)
    }

    @Test
    fun `given valid csv data with duplicate api levels - filters out duplicates`() {
        val csvData: String = """
        Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        motorola,aljeter,Motorola,moto g(6) play,3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf(26, 28), parsedDevices.first().sdkVersions)
    }

    @Test
    fun `given valid csv data with duplicate abis - filters out duplicates`() {
        val csvData: String = """
        Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        motorola,aljeter,Motorola,moto g(6) play,3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a;armeabi;armeabi-v7a,26;28,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf("armeabi", "armeabi-v7a"), parsedDevices.first().abis)
    }

    @Test
    fun `given valid csv data with duplicate opengl versions - filters out duplicates`() {
        val csvData: String = """
        Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        motorola,aljeter,Motorola,moto g(6) play,3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a;armeabi;armeabi-v7a,26;28,3.1;3.2;3.1;3.0
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf("3.0", "3.1", "3.2"), parsedDevices.first().openGlEsVersions)
    }

    @Test
    fun `given valid csv data row parses device attributes`() {
        val csvData: String = """
        Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,32;33,3.2
        """.trimIndent()

        val parsedDevices = sut.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)
        val device = parsedDevices.first()

        assertEquals("google", device.brand)
        assertEquals("coral", device.device)
        assertEquals("Google", device.manufacturer)
        assertEquals("Google", device.manufacturer)
        assertEquals("Pixel 4 XL", device.modelName)
        assertEquals("5730MB", device.ram)
        assertEquals("Phone", device.formFactor)
        assertEquals("Qualcomm SDM855", device.processorName)
        assertEquals("Qualcomm Adreno 640 (585 MHz)", device.gpu)
        assertEquals(listOf("1440x3040"), device.screenSizes)
        assertEquals(listOf(560), device.screenDensities)
        assertEquals(listOf("arm64-v8a", "armeabi", "armeabi-v7a"), device.abis)
        assertEquals(listOf(32, 33), device.sdkVersions)
        assertEquals(listOf("3.2"), device.openGlEsVersions)
    }

    @Test
    fun `given all catalog data is loaded then parsers all devices`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = sut.parseDeviceCatalogData(csvFileContent)

        assertTrue(parsedDevices.size > 1000, "Total parsed devices should be more than thousand")
    }

    @Test
    fun `given parsed devices available - can converts to JSON`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = sut.parseDeviceCatalogData(csvFileContent)

        val gson = Gson()
        val convertedJson = gson.toJson(parsedDevices)

        assertTrue(convertedJson.length > 1_000_000)

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // Use following to write file to resources
        /*val resourceDirectory: Path = Paths.get("src", "test", "resources")
        val resDir = resourceDirectory.toFile()
        // UNIX: src/test/resources
        // WINDOWS: src\test\resources
        assertTrue(resDir.absolutePath.endsWith("src${File.separator}test${File.separator}resources")) // src\test\resources

        val jsonFile: File = File(resDir, "android-devices-catalog-min.json")
        jsonFile.writeText(convertedJson)*/

        // TIP - Can't edit large 4MB+ JSON file and can't format it? 
        // --------------------------------------------------------------
        // Follow the guide from https://www.jetbrains.com/help/objc/configuring-file-size-limit.html#file-size-limit
        // And set the following custom config
        // idea.max.intellisense.filesize=10000 # 10MB
    }
}
