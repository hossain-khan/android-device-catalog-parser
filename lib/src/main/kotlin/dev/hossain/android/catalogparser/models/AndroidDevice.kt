package dev.hossain.android.catalogparser.models

/**
 * Model class for
 */
class AndroidDevice(
    val manufacturer: String,
    val modelName: String,
    val modelCode: String,
    val ram: String,
    val formFactor: String,
    val processorName: String,
    val screenSizes: List<String>,
    val screenDensities: List<Int>,
    val abis: String,
    val sdkVersions: List<Int>,
    val openGlEsVersions: List<String>
)