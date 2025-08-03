package dev.hossain.android.catalogparser.models

/**
 * Model class for Android Device with device attributes found in the official Device Catalog.
 * See https://play.google.com/console/about/devicecatalog/ to get latest version.
 */
class AndroidDevice(
    /**
     * Manufacturer brand code.
     * Examples: google, htc, lge, Lenovo, motorola
     */
    val brand: String,
    /**
     * Examples: coral, htc_a50aml, SW4H_FF, luv20ts, falcon_umts
     */
    val device: String,
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
     * Device RAM
     * Examples: 934MB, 459MB, 1942MB, 3770MB
     */
    val ram: String,
    /**
     * Device form factor categorizing the type and intended use case of the device.
     *
     * This enum value represents the physical form and primary interaction model
     * of the device, such as Phone, Tablet, TV, Wearable, etc.
     *
     * @see FormFactor for detailed descriptions of each form factor type
     */
    val formFactor: FormFactor,
    /**
     * Examples: Mediatek MT6572A, Qualcomm MSM8909, Rockchip RK3326, Spreadtrum SC9832A
     */
    val processorName: String,
    /**
     * GPU processor name.
     * Examples: Qualcomm Adreno 640 (585 MHz), ARM Mali 400 (400 MHz)
     */
    val gpu: String,
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
    val openGlEsVersions: List<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AndroidDevice

        if (brand != other.brand) return false
        if (device != other.device) return false
        if (manufacturer != other.manufacturer) return false
        if (modelName != other.modelName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = brand.hashCode()
        result = 31 * result + device.hashCode()
        result = 31 * result + manufacturer.hashCode()
        result = 31 * result + modelName.hashCode()
        return result
    }
}
