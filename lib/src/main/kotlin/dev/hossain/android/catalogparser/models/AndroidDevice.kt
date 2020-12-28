package dev.hossain.android.catalogparser.models

/**
 * Model class for Android Device with device attributes found in the official Device Catalog.
 * See https://play.google.com/console/about/devicecatalog/ to get latest version.
 */
class AndroidDevice(
    /**
     * Device manufacturer name.
     * Examples: Acer, LG, Samsung
     */
    val manufacturer: String,

    /**
     * Device model name (usually public facing)
     * Examples: Iconia One 7, S13, ZenFone 4 (ZE554KL)
     */
    val modelName: String,

    /**
     * Device model code internally used by manufacturer
     * Examples: ASUS_Z01KD_3, K00X_1, BLU_NEO_X_LTE
     */
    val modelCode: String,

    /**
     * Device RAM
     * Examples: 934MB, 459MB, 1942MB, 3770MB
     */
    val ram: String,

    /**
     * Device form factor.
     * Examples: Phone, TV, Tablet
     */
    val formFactor: String,

    /**
     * Examples: Mediatek MT6572A, Qualcomm MSM8909, Rockchip RK3326, Spreadtrum SC9832A
     */
    val processorName: String,

    /**
     * Examples: 1024x600, 1366x768, 480x854, 800x1280
     */
    val screenSizes: List<String>,

    /**
     * Examples: 160, 213, 240, 300, 320
     */
    val screenDensities: List<Int>,

    /**
     * Examples: arm64-v8a, armeabi, armeabi-v7a
     */
    val abis: List<String>,

    /**
     * Examples: 23, 24, 27
     */
    val sdkVersions: List<Int>,

    /**
     * Examples: 2.0, 3.0, 3.1, 3.2
     */
    val openGlEsVersions: List<String>
)