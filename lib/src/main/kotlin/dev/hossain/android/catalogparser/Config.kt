package dev.hossain.android.catalogparser

/**
 * Contains CSV header names found in Google Play Device Catalog
 * See https://play.google.com/console/about/devicecatalog/ to get latest version.
 *
 * Current Headers and sample data (as of Dec 2023)
 * ```
 * Brand,Device,Manufacturer,Model Name,RAM (TotalMem),Form Factor,System on Chip,GPU,Screen Sizes,Screen Densities,ABIs,Android SDK Versions,OpenGL ES Versions,Install base,User-perceived ANR rate,User-perceived crash rate
 * google,coral,Google,Pixel 4 XL,5730MB,Phone,Qualcomm SDM855,Qualcomm Adreno 640 (585 MHz),1440x3040,560,arm64-v8a;armeabi;armeabi-v7a,33,3.2,0,0.00%,0.00%
 * google,cheetah,Google,Pixel 7 Pro,11972-12028MB,Phone,Google Tensor,ARM Mali G78 (848 MHz),1440x3120,560,arm64-v8a,33;34,3.2,0,0.00%,0.00%
 * ```
 */
object Config {
    const val CSV_MULTI_VALUE_SEPARATOR = ";"

    const val CSV_KEY_BRAND = "Brand"
    const val CSV_KEY_DEVICE = "Device"
    const val CSV_KEY_MANUFACTURER = "Manufacturer"
    const val CSV_KEY_MODEL_NAME = "Model Name"
    const val CSV_KEY_RAM = "RAM (TotalMem)"
    const val CSV_KEY_FORM_FACTOR = "Form Factor"
    const val CSV_KEY_SOC = "System on Chip"
    const val CSV_KEY_SCREEN_GPU = "GPU"
    const val CSV_KEY_SCREEN_SIZES = "Screen Sizes"
    const val CSV_KEY_SCREEN_DENSITIES = "Screen Densities"
    const val CSV_KEY_ABIS = "ABIs"
    const val CSV_KEY_SDK_VERSIONS = "Android SDK Versions"
    const val CSV_KEY_OPENGL_ES_VERSIONS = "OpenGL ES Versions"
}