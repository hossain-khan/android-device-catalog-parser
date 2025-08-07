package dev.hossain.android.catalogparser

import com.google.gson.Gson
import dev.hossain.android.catalogparser.DataSanitizer.sanitizeDeviceRam
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.android.catalogparser.models.ParseResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests the Android device catalog CSV [Parser].
 */
class ParserTest {
    @Test
    fun `given no csv header fails to parse devices`() {
        val csvData: String =
            """
            motorola,ali,Motorola,moto g(6),2994-3793MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,26;28,3.2,0,1.00%,0.00%
            motorola,ali_n,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,28,3.2,0,0.00%,0.00%
            motorola,aljeter,Motorola,moto g(6) play,3014-3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(0, parsedDevices.size)
    }

    @Test
    fun `given valid csv data parses device list`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            motorola,ali,Motorola,moto g(6),2994-3793MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,26;28,3.2,0,0.00%,0.00%
            motorola,ali_n,Motorola,moto g(6),2994MB,Phone,Qualcomm SDM450,Qualcomm Adreno 506 (600 MHz),1080x2160,480,armeabi;armeabi-v7a,28,3.2,0,0.00%,0.00%
            motorola,aljeter,Motorola,moto g(6) play,3014-3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(3, parsedDevices.size)
    }

    @Test
    fun `given valid csv data with duplicate api levels - filters out duplicates`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            motorola,aljeter,Motorola,moto g(6) play,3014-3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf(26, 28), parsedDevices.first().sdkVersions)
    }

    @Test
    fun `given valid csv data with duplicate abis - filters out duplicates`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            motorola,aljeter,Motorola,moto g(6) play,3014-3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf("armeabi", "armeabi-v7a"), parsedDevices.first().abis)
    }

    @Test
    fun `given valid csv data with duplicate opengl versions - filters out duplicates`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            motorola,aljeter,Motorola,moto g(6) play,3014-3018MB,Phone,Qualcomm MSM8937,Qualcomm Adreno 505 (450 MHz),720x1440,320,armeabi;armeabi-v7a,26;28,3.1;3.2;3.1,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals(listOf("3.1", "3.2"), parsedDevices.first().openGlEsVersions)
    }

    @Test
    fun `given valid csv data with ram range - takes the max ram value`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            samsung,hero2lte,Samsung,Galaxy S7 edge,3705-3735MB,Phone,Samsung Exynos 8890,ARM Mali T880 (650 MHz),1080x1920;1440x2560,480;640,arm64-v8a;armeabi;armeabi-v7a,23;24;26,3.1;3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)

        assertEquals("3735MB", sanitizeDeviceRam(parsedDevices.first().ram))
    }

    @Test
    fun `given valid csv data row parses device attributes`() {
        val csvData: String =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
            """.trimIndent()

        val parsedDevices = Parser.parseDeviceCatalogData(csvData)

        assertEquals(1, parsedDevices.size)
        val device = parsedDevices.first()

        assertEquals("google", device.brand)
        assertEquals("coral", device.device)
        assertEquals("Google", device.manufacturer)
        assertEquals("Google", device.manufacturer)
        assertEquals("Pixel 4 XL", device.modelName)
        assertEquals("5730MB", device.ram)
        assertEquals(FormFactor.PHONE, device.formFactor)
        assertEquals("Qualcomm SDM855", device.processorName)
        assertEquals("Qualcomm Adreno 640 (585 MHz)", device.gpu)
        assertEquals(listOf("1440x3040"), device.screenSizes)
        assertEquals(listOf(560), device.screenDensities)
        assertEquals(listOf("arm64-v8a", "armeabi", "armeabi-v7a"), device.abis)
        assertEquals(listOf(33), device.sdkVersions)
        assertEquals(listOf("3.2"), device.openGlEsVersions)
    }

    @Test
    fun `given all catalog data is loaded then parsers all devices`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = Parser.parseDeviceCatalogData(csvFileContent)

        assertTrue(parsedDevices.size > 1000, "Total parsed devices should be more than thousand")
    }

    @Test
    fun `given parsed devices available - can converts to JSON`() {
        val csvFileContent = javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()

        val parsedDevices: List<AndroidDevice> = Parser.parseDeviceCatalogData(csvFileContent)

        val gson = Gson()
        val convertedJson = gson.toJson(parsedDevices)

        assertTrue(convertedJson.length > 1_000_000)

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // Use following to write file to resources

        /*
        //import java.io.File
        //import java.nio.file.Path
        //import java.nio.file.Paths
        val resourceDirectory: Path = Paths.get("src", "test", "resources")
        val resDir = resourceDirectory.toFile()
        // UNIX: src/test/resources
        // WINDOWS: src\test\resources
        assertTrue(resDir.absolutePath.endsWith("src${File.separator}test${File.separator}resources"))

        val jsonFile: File = File(resDir, "android-devices-catalog-min.json")
        jsonFile.writeText(convertedJson)
         */

        // TIP - Can't edit large 10MB+ JSON file and can't format it?
        // --------------------------------------------------------------
        // Follow the guide from https://www.jetbrains.com/help/objc/configuring-file-size-limit.html#file-size-limit
        // And set the following custom config
        // idea.max.intellisense.filesize=15000 # 15MB
        //
        // Or use easy way:
        // Use "Help | Edit Custom Properties..." action to create an `idea.properties` file in your config directory.
        // And add the config above
    }

    // region Tests by AI

    @Test
    fun `parseDeviceCatalogData returns empty list when csvContent is empty`() {
        val csvContent = ""
        val expected = emptyList<AndroidDevice>()
        val actual = Parser.parseDeviceCatalogData(csvContent)
        assertEquals(expected, actual)
    }

    @Test
    fun `parseDeviceCatalogData returns list of AndroidDevice when csvContent is valid`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
            """.trimIndent()
        val actual = Parser.parseDeviceCatalogData(csvContent)
        assertEquals(1, actual.size)
    }

    @Test
    fun `parseDeviceCatalogData returns list of AndroidDevice with correct attributes when csvContent is valid`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
            """.trimIndent()
        val actual = Parser.parseDeviceCatalogData(csvContent)
        val expectedDevice =
            AndroidDevice(
                brand = "google",
                device = "coral",
                manufacturer = "Google",
                modelName = "Pixel 4 XL",
                ram = "5730MB",
                formFactor = FormFactor.PHONE,
                processorName = "Qualcomm SDM855",
                gpu = "Qualcomm Adreno 640 (585 MHz)",
                screenSizes = listOf("1440x3040"),
                screenDensities = listOf(560),
                abis = listOf("arm64-v8a", "armeabi", "armeabi-v7a"),
                sdkVersions = listOf(33),
                openGlEsVersions = listOf("3.2"),
            )
        assertEquals(expectedDevice, actual[0])
    }

    // endregion Tests by AI

    // region FormFactor Integration Tests

    @Test
    fun `parseDeviceCatalogData correctly parses all form factors`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            samsung,tab1,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            google,sabrina,Google,Chromecast with Google TV,2048MB,TV,ARM MT8167A,IMG GE8300,1920x1080,213,arm64-v8a,29,3.2,0,0.00%,0.00%
            samsung,watch1,Samsung,Galaxy Watch 4,1536MB,Wearable,Samsung Exynos W920,ARM Mali-G68 MP2,450x450,320,arm64-v8a,30,3.2,0,0.00%,0.00%
            volvo,car1,Volvo,XC90,4096MB,Android Automotive,Qualcomm SDM820,Qualcomm Adreno 530,1920x1200,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            google,chromebook1,Google,Pixelbook Go,8192MB,Chromebook,Intel Core m3,Intel UHD Graphics 615,1920x1080,166,x86_64,28,3.2,0,0.00%,0.00%
            google,pc1,Google,Play Games on PC,16384MB,Google Play Games on PC,Intel i7,NVIDIA RTX,1920x1080,96,x86_64,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val actual = Parser.parseDeviceCatalogData(csvContent)
        assertEquals(7, actual.size)

        // Verify each form factor is parsed correctly
        assertEquals(FormFactor.PHONE, actual[0].formFactor)
        assertEquals(FormFactor.TABLET, actual[1].formFactor)
        assertEquals(FormFactor.TV, actual[2].formFactor)
        assertEquals(FormFactor.WEARABLE, actual[3].formFactor)
        assertEquals(FormFactor.ANDROID_AUTOMOTIVE, actual[4].formFactor)
        assertEquals(FormFactor.CHROMEBOOK, actual[5].formFactor)
        assertEquals(FormFactor.GOOGLE_PLAY_GAMES_ON_PC, actual[6].formFactor)
    }

    @Test
    fun `parseDeviceCatalogData handles unknown form factor gracefully`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            unknown,device1,Unknown,Unknown Device,1024MB,Unknown Form Factor,Unknown Chip,Unknown GPU,1920x1080,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val actual = Parser.parseDeviceCatalogData(csvContent)

        // Should skip devices with unknown form factors
        assertEquals(0, actual.size)
    }

    @Test
    fun `parseDeviceCatalogData filters out devices with invalid form factors`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            unknown,device1,Unknown,Unknown Device,1024MB,Invalid Form Factor,Unknown Chip,Unknown GPU,1920x1080,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            samsung,tab1,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val actual = Parser.parseDeviceCatalogData(csvContent)

        // Should only parse valid form factors
        assertEquals(2, actual.size)
        assertEquals(FormFactor.PHONE, actual[0].formFactor)
        assertEquals(FormFactor.TABLET, actual[1].formFactor)
    }

    @Test
    fun `parseDeviceCatalogData maintains form factor case sensitivity`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            samsung,tab1,Samsung,Galaxy Tab S7,8192MB,TABLET,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val actual = Parser.parseDeviceCatalogData(csvContent)

        // Should filter out case-mismatched form factors (FormFactor.fromCsvValueOrNull is case-sensitive)
        assertEquals(0, actual.size)
    }

    // endregion FormFactor Integration Tests

    // region Enhanced Parser Tests with Statistics

    @Test
    fun `parseDeviceCatalogDataWithStats returns full statistics for valid data`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
            samsung,tab1,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(2, result.totalRows)
        assertEquals(2, result.successfulCount)
        assertEquals(0, result.discardedCount)
        assertEquals(100.0, result.successRate)
        assertEquals(emptyMap<String, Int>(), result.discardReasons)
        assertEquals(2, result.devices.size)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats tracks missing required fields`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            motorola,device3,,Device 3,1024MB,Phone,Qualcomm,GPU,1920x1080,320,arm64-v8a,28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(3, result.totalRows)
        assertEquals(1, result.successfulCount)
        assertEquals(2, result.discardedCount)
        assertEquals(33.33, result.successRate, 0.01)

        val expectedReasons =
            mapOf(
                "Missing required field: Brand" to 1,
                "Missing required field: Device" to 1,
                "Missing required field: Manufacturer" to 1,
            )
        assertEquals(expectedReasons, result.discardReasons)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats tracks unknown form factors`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            unknown,device1,Unknown,Device 1,1024MB,Unknown Form,Unknown Chip,Unknown GPU,1920x1080,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            another,device2,Another,Device 2,2048MB,Invalid Type,Another Chip,Another GPU,1280x720,240,arm64-v8a,29,3.2,0,0.00%,0.00%
            """.trimIndent()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(3, result.totalRows)
        assertEquals(1, result.successfulCount)
        assertEquals(2, result.discardedCount)
        assertEquals(33.33, result.successRate, 0.01)

        val expectedReasons =
            mapOf(
                "Unknown form factor: Unknown Form" to 1,
                "Unknown form factor: Invalid Type" to 1,
            )
        assertEquals(expectedReasons, result.discardReasons)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats handles mixed validation failures`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            brand2,device2,Manufacturer2,Model2,1024MB,Unknown Form,Processor2,GPU2,1920x1080,320,arm64-v8a,28,3.2,0,0.00%,0.00%
            brand3,device3,Manufacturer3,,2048MB,Phone,Processor3,,1280x720,240,arm64-v8a,29,3.2,0,0.00%,0.00%
            """.trimIndent()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(4, result.totalRows)
        assertEquals(1, result.successfulCount)
        assertEquals(3, result.discardedCount)
        assertEquals(25.0, result.successRate)

        val expectedReasons =
            mapOf(
                "Missing required field: Brand" to 1,
                "Missing required field: Device" to 1,
                "Missing required field: RAM (TotalMem)" to 1,
                "Unknown form factor: Unknown Form" to 1,
                "Missing required field: Model Name" to 1,
                "Missing required field: GPU" to 1,
            )
        assertEquals(expectedReasons, result.discardReasons)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats handles empty CSV content`() {
        val csvContent = ""

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(0, result.totalRows)
        assertEquals(0, result.successfulCount)
        assertEquals(0, result.discardedCount)
        assertEquals(0.0, result.successRate)
        assertEquals(emptyMap<String, Int>(), result.discardReasons)
        assertEquals(emptyList<AndroidDevice>(), result.devices)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats handles CSV with only header`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            """.trimIndent()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(0, result.totalRows)
        assertEquals(0, result.successfulCount)
        assertEquals(0, result.discardedCount)
        assertEquals(0.0, result.successRate)
        assertEquals(emptyMap<String, Int>(), result.discardReasons)
        assertEquals(emptyList<AndroidDevice>(), result.devices)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats returns same devices as original method for valid data`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
            samsung,tab1,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val originalResult = Parser.parseDeviceCatalogData(csvContent)
        val enhancedResult = Parser.parseDeviceCatalogDataWithStats(csvContent)

        assertEquals(originalResult, enhancedResult.devices)
    }

    // endregion Enhanced Parser Tests with Statistics

    // region ParserConfig Integration Tests

    @Test
    fun `parseDeviceCatalogData with default config should behave like original implementation`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val defaultResult = Parser.parseDeviceCatalogData(csvContent)
        val explicitDefaultResult = Parser.parseDeviceCatalogData(csvContent, ParserConfig.DEFAULT)

        assertEquals(defaultResult, explicitDefaultResult)
        assertEquals(1, defaultResult.size) // Should discard row with missing Brand/Device
    }

    @Test
    fun `parseDeviceCatalogData with useDefaultsForMissingFields should include previously discarded rows`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .defaultStringValue("Unknown")
                .build()

        val result = Parser.parseDeviceCatalogData(csvContent, config)

        assertEquals(2, result.size)

        // First device should be unchanged
        assertEquals("google", result[0].brand)
        assertEquals("coral", result[0].device)

        // Second device should use defaults for missing fields
        assertEquals("Unknown", result[1].brand)
        assertEquals("Unknown", result[1].device)
        assertEquals("Samsung", result[1].manufacturer)
    }

    @Test
    fun `parseDeviceCatalogData with defaultFormFactor should include unknown form factors`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            unknown,device1,Unknown,Device 1,1024MB,UnknownFormFactor,Unknown Chip,Unknown GPU,1920x1080,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .defaultFormFactor(FormFactor.PHONE)
                .build()

        val result = Parser.parseDeviceCatalogData(csvContent, config)

        assertEquals(2, result.size)
        assertEquals(FormFactor.PHONE, result[0].formFactor)
        assertEquals(FormFactor.PHONE, result[1].formFactor) // Should use default
    }

    @Test
    fun `parseDeviceCatalogData with combined config should handle both missing fields and unknown form factors`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Missing,Missing Device,,WeirdFormFactor,,,,,,,,,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .defaultStringValue("N/A")
                .defaultFormFactor(FormFactor.TABLET)
                .build()

        val result = Parser.parseDeviceCatalogData(csvContent, config)

        assertEquals(2, result.size)

        // Second device should use all defaults
        assertEquals("N/A", result[1].brand)
        assertEquals("N/A", result[1].device)
        assertEquals("Missing", result[1].manufacturer)
        assertEquals("Missing Device", result[1].modelName)
        assertEquals("N/A", result[1].ram)
        assertEquals(FormFactor.TABLET, result[1].formFactor)
        assertEquals("N/A", result[1].processorName)
        assertEquals("N/A", result[1].gpu)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats with default config should behave like original implementation`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val defaultResult = Parser.parseDeviceCatalogDataWithStats(csvContent)
        val explicitDefaultResult = Parser.parseDeviceCatalogDataWithStats(csvContent, ParserConfig.DEFAULT)

        assertEquals(defaultResult.devices, explicitDefaultResult.devices)
        assertEquals(defaultResult.totalRows, explicitDefaultResult.totalRows)
        assertEquals(defaultResult.discardedCount, explicitDefaultResult.discardedCount)
        assertEquals(defaultResult.discardReasons, explicitDefaultResult.discardReasons)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats with useDefaultsForMissingFields should not track missing field discards`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .defaultStringValue("Unknown")
                .build()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent, config)

        assertEquals(2, result.totalRows)
        assertEquals(2, result.successfulCount)
        assertEquals(0, result.discardedCount)
        assertEquals(100.0, result.successRate)
        assertEquals(emptyMap<String, Int>(), result.discardReasons)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats with defaultFormFactor should not track unknown form factor discards`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            unknown,device1,Unknown,Device 1,1024MB,UnknownFormFactor,Unknown Chip,Unknown GPU,1920x1080,160,arm64-v8a,28,3.2,0,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .defaultFormFactor(FormFactor.WEARABLE)
                .build()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent, config)

        assertEquals(2, result.totalRows)
        assertEquals(2, result.successfulCount)
        assertEquals(0, result.discardedCount)
        assertEquals(100.0, result.successRate)
        assertEquals(emptyMap<String, Int>(), result.discardReasons)

        // Verify the default form factor was used
        assertEquals(FormFactor.WEARABLE, result.devices[1].formFactor)
    }

    @Test
    fun `parseDeviceCatalogDataWithStats should handle mixed scenarios with partial configuration`() {
        val csvContent =
            """
            Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
            google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a,33,3.2,0,0.00%,0.00%
            ,,Samsung,Galaxy Tab S7,8192MB,Tablet,Qualcomm SDM865+,Qualcomm Adreno 650,2560x1600,287,arm64-v8a,30,3.2,0,0.00%,0.00%
            valid,device2,Valid,Device 2,2048MB,UnknownFormFactor,Valid Chip,Valid GPU,1280x720,240,arm64-v8a,29,3.2,0,0.00%,0.00%
            """.trimIndent()

        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .defaultStringValue("DefaultValue")
                // Note: No defaultFormFactor configured
                .build()

        val result = Parser.parseDeviceCatalogDataWithStats(csvContent, config)

        assertEquals(3, result.totalRows)
        assertEquals(2, result.successfulCount) // Third row discarded due to unknown form factor
        assertEquals(1, result.discardedCount)
        assertEquals(66.67, result.successRate, 0.01)

        val expectedReasons =
            mapOf(
                "Unknown form factor: UnknownFormFactor" to 1,
            )
        assertEquals(expectedReasons, result.discardReasons)

        // Verify the first row uses defaults for missing fields
        assertEquals("DefaultValue", result.devices[1].brand)
        assertEquals("DefaultValue", result.devices[1].device)
    }

    // endregion ParserConfig Integration Tests
}
