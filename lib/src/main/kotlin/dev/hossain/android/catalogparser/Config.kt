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

    /**
     * This constant represents the CSV header for the brand of the device.
     * Example: "google"
     */
    const val CSV_KEY_BRAND = "Brand"

    /**
     * This constant represents the CSV header for the device identifier.
     * Example: "coral"
     */
    const val CSV_KEY_DEVICE = "Device"

    /**
     * This constant represents the CSV header for the manufacturer of the device.
     * Example: "Google"
     */
    const val CSV_KEY_MANUFACTURER = "Manufacturer"

    /**
     * This constant represents the CSV header for the model name of the device.
     * Example: "Pixel 4 XL"
     */
    const val CSV_KEY_MODEL_NAME = "Model Name"

    /**
     * This constant represents the CSV header for the total memory (RAM) of the device.
     * Example: "5730MB"
     */
    const val CSV_KEY_RAM = "RAM (TotalMem)"

    /**
     * This constant represents the CSV header for the form factor of the device.
     * Example: "Phone"
     */
    const val CSV_KEY_FORM_FACTOR = "Form Factor"

    /**
     * This constant represents the CSV header for the system on chip of the device.
     * Example: "Qualcomm SDM855"
     */
    const val CSV_KEY_SOC = "System on Chip"

    /**
     * This constant represents the CSV header for the GPU of the device.
     * Example: "Qualcomm Adreno 640 (585 MHz)"
     */
    const val CSV_KEY_SCREEN_GPU = "GPU"

    /**
     * This constant represents the CSV header for the screen sizes of the device.
     * Example: "1440x3040"
     */
    const val CSV_KEY_SCREEN_SIZES = "Screen Sizes"

    /**
     * This constant represents the CSV header for the screen densities of the device.
     * Example: "560"
     */
    const val CSV_KEY_SCREEN_DENSITIES = "Screen Densities"

    /**
     * This constant represents the CSV header for the ABIs of the device.
     * Example: "arm64-v8a;armeabi;armeabi-v7a"
     */
    const val CSV_KEY_ABIS = "ABIs"

    /**
     * This constant represents the CSV header for the Android SDK versions supported by the device.
     * Example: "33"
     */
    const val CSV_KEY_SDK_VERSIONS = "Android SDK Versions"

    /**
     * This constant represents the CSV header for the OpenGL ES versions supported by the device.
     * Example: "3.2"
     */
    const val CSV_KEY_OPENGL_ES_VERSIONS = "OpenGL ES Versions"
}
