package dev.hossain.android.catalogparser

import kotlin.test.Test
import kotlin.test.assertTrue

class ParserTest {
    private val testData: String = """
        Manufacturer,Model Name,Model Code,RAM (TotalMem),Form Factor,System on Chip,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions
        10.or,D,10or_D,2868-2874MB,Phone,Qualcomm MSM8917,720x1280,320,arm64-v8a;armeabi;armeabi-v7a,25;27,3.0
        10.or,E,E,1857-2846MB,Phone,Qualcomm MSM8937,1080x1920,480,arm64-v8a;armeabi;armeabi-v7a,25;27,3.2
        10.or,G,G,2851-3582MB,Phone,Qualcomm MSM8953,1080x1920,480,arm64-v8a;armeabi;armeabi-v7a,25;27,3.2
        10.or,10or_G2,G2,3742-5747MB,Phone,Qualcomm SDM636,1080x2246,480,arm64-v8a;armeabi;armeabi-v7a,27,3.2
        3222222 Satelital,G706,G706,920MB,Tablet,Mediatek MT8321,600x1024,160,armeabi;armeabi-v7a,27,2.0
    """.trimIndent()

    @Test
    fun testParsing() {
        val parser = Parser()
        val parseDeviceCatalogData = parser.parseDeviceCatalogData(testData)

        println(parseDeviceCatalogData)
        assertTrue(parseDeviceCatalogData.size == 5, "someLibraryMethod should return 'true'")
    }


}
