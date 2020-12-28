package dev.hossain.android.catalogparser

/**
 * Contains CSV header names found in Google Play Device Catalog
 * See https://play.google.com/console/about/devicecatalog/ to get latest version.
 */
object Config {
    const val CSV_MULTI_VALUE_SEPARATOR = ";"

    const val CSV_KEY_MANUFACTURER = "Manufacturer"
    const val CSV_KEY_MODEL_NAME = "Model Name"
    const val CSV_KEY_MODEL_CODE = "Model Code"
    const val CSV_KEY_RAM = "RAM (TotalMem)"
    const val CSV_KEY_FORM_FACTOR = "Form Factor"
    const val CSV_KEY_SOC = "System on Chip"
    const val CSV_KEY_SCREEN_SIZES = "Screen Sizes"
    const val CSV_KEY_SCREEN_DENSITIES = "Screen Densities"
    const val CSV_KEY_ABIS = "ABIs"
    const val CSV_KEY_SDK_VERSIONS = "Android SDK Versions"
    const val CSV_KEY_OPENGL_ES_VERSIONS = "OpenGL ES Versions"
}