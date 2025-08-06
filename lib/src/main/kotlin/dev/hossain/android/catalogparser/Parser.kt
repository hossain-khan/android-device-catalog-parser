package dev.hossain.android.catalogparser

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dev.hossain.android.catalogparser.Config.CSV_KEY_ABIS
import dev.hossain.android.catalogparser.Config.CSV_KEY_BRAND
import dev.hossain.android.catalogparser.Config.CSV_KEY_DEVICE
import dev.hossain.android.catalogparser.Config.CSV_KEY_FORM_FACTOR
import dev.hossain.android.catalogparser.Config.CSV_KEY_MANUFACTURER
import dev.hossain.android.catalogparser.Config.CSV_KEY_MODEL_NAME
import dev.hossain.android.catalogparser.Config.CSV_KEY_OPENGL_ES_VERSIONS
import dev.hossain.android.catalogparser.Config.CSV_KEY_RAM
import dev.hossain.android.catalogparser.Config.CSV_KEY_SCREEN_DENSITIES
import dev.hossain.android.catalogparser.Config.CSV_KEY_SCREEN_GPU
import dev.hossain.android.catalogparser.Config.CSV_KEY_SCREEN_SIZES
import dev.hossain.android.catalogparser.Config.CSV_KEY_SDK_VERSIONS
import dev.hossain.android.catalogparser.Config.CSV_KEY_SOC
import dev.hossain.android.catalogparser.Config.CSV_MULTI_VALUE_SEPARATOR
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.android.catalogparser.models.ParseResult

/**
 * CSV parser for Google Play Device Catalog found in Google Play Console
 * See https://play.google.com/console/about/devicecatalog/ to get latest version.
 * @see Config
 */
class Parser {
    private val csvReader: CsvReader = csvReader()

    /**
     * Given Android Device Catalog CSV data, parses to list of [AndroidDevice].
     */
    fun parseDeviceCatalogData(csvContent: String): List<AndroidDevice> {
        val deviceRows: List<Map<String, String>> = csvReader.readAllWithHeader(csvContent)

        return deviceRows
            .map { rowData ->
                val brand = rowData[CSV_KEY_BRAND]
                val device = rowData[CSV_KEY_DEVICE]
                val manufacturer = rowData[CSV_KEY_MANUFACTURER]
                val modelName = rowData[CSV_KEY_MODEL_NAME]
                val ram = rowData[CSV_KEY_RAM]
                val formFactorString = rowData[CSV_KEY_FORM_FACTOR]
                val processorName = rowData[CSV_KEY_SOC]
                val gpu = rowData[CSV_KEY_SCREEN_GPU]
                val screenSizes = rowData[CSV_KEY_SCREEN_SIZES]
                val screenDensities = rowData[CSV_KEY_SCREEN_DENSITIES]
                val abis = rowData[CSV_KEY_ABIS]
                val sdkVersions = rowData[CSV_KEY_SDK_VERSIONS]
                val openGlEsVersions = rowData[CSV_KEY_OPENGL_ES_VERSIONS]

                // Validates non-null values
                if (brand.isNullOrBlank() ||
                    device.isNullOrBlank() ||
                    manufacturer.isNullOrBlank() ||
                    modelName.isNullOrBlank() ||
                    ram.isNullOrBlank() ||
                    formFactorString.isNullOrBlank() ||
                    processorName.isNullOrBlank() ||
                    gpu.isNullOrBlank() ||
                    screenSizes.isNullOrBlank() ||
                    screenDensities.isNullOrBlank() ||
                    abis.isNullOrBlank() ||
                    sdkVersions.isNullOrBlank() ||
                    openGlEsVersions.isNullOrBlank()
                ) {
                    return@map null
                }

                // Convert form factor string to enum, skip record if unknown
                val formFactor = FormFactor.fromValueOrNull(formFactorString)
                if (formFactor == null) {
                    // Log or handle unknown form factor - for now, skip the record
                    return@map null
                }

                return@map AndroidDevice(
                    brand = brand,
                    device = device,
                    manufacturer = manufacturer,
                    modelName = modelName,
                    ram = ram,
                    formFactor = formFactor,
                    processorName = processorName,
                    gpu = gpu,
                    screenSizes =
                        screenSizes
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    screenDensities =
                        screenDensities
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .map { it.toInt() },
                    abis =
                        abis
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    sdkVersions =
                        sdkVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .map { it.toInt() },
                    openGlEsVersions =
                        openGlEsVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                )
            }.filterNotNull()
    }

    /**
     * Enhanced version of [parseDeviceCatalogData] that provides detailed parsing statistics.
     * 
     * Given Android Device Catalog CSV data, parses to [ParseResult] containing both
     * the successfully parsed devices and detailed information about discarded records.
     * 
     * @param csvContent CSV content string with header row
     * @return [ParseResult] containing parsed devices and parsing statistics
     */
    fun parseDeviceCatalogDataWithStats(csvContent: String): ParseResult {
        val deviceRows: List<Map<String, String>> = csvReader.readAllWithHeader(csvContent)
        
        val discardReasons = mutableMapOf<String, Int>()
        val devices = mutableListOf<AndroidDevice>()
        var discardedCount = 0
        
        deviceRows.forEach { rowData ->
            val brand = rowData[CSV_KEY_BRAND]
            val device = rowData[CSV_KEY_DEVICE]
            val manufacturer = rowData[CSV_KEY_MANUFACTURER]
            val modelName = rowData[CSV_KEY_MODEL_NAME]
            val ram = rowData[CSV_KEY_RAM]
            val formFactorString = rowData[CSV_KEY_FORM_FACTOR]
            val processorName = rowData[CSV_KEY_SOC]
            val gpu = rowData[CSV_KEY_SCREEN_GPU]
            val screenSizes = rowData[CSV_KEY_SCREEN_SIZES]
            val screenDensities = rowData[CSV_KEY_SCREEN_DENSITIES]
            val abis = rowData[CSV_KEY_ABIS]
            val sdkVersions = rowData[CSV_KEY_SDK_VERSIONS]
            val openGlEsVersions = rowData[CSV_KEY_OPENGL_ES_VERSIONS]

            // Track missing required fields
            val missingFields = mutableListOf<String>()
            if (brand.isNullOrBlank()) missingFields.add("Brand")
            if (device.isNullOrBlank()) missingFields.add("Device")
            if (manufacturer.isNullOrBlank()) missingFields.add("Manufacturer")
            if (modelName.isNullOrBlank()) missingFields.add("Model Name")
            if (ram.isNullOrBlank()) missingFields.add("RAM (TotalMem)")
            if (formFactorString.isNullOrBlank()) missingFields.add("Form Factor")
            if (processorName.isNullOrBlank()) missingFields.add("System on Chip")
            if (gpu.isNullOrBlank()) missingFields.add("GPU")
            if (screenSizes.isNullOrBlank()) missingFields.add("Screen Sizes")
            if (screenDensities.isNullOrBlank()) missingFields.add("Screen Densities")
            if (abis.isNullOrBlank()) missingFields.add("ABIs")
            if (sdkVersions.isNullOrBlank()) missingFields.add("Android SDK Versions")
            if (openGlEsVersions.isNullOrBlank()) missingFields.add("OpenGL ES Versions")

            if (missingFields.isNotEmpty()) {
                discardedCount++
                // Track each missing field reason
                missingFields.forEach { field ->
                    val reason = "Missing required field: $field"
                    discardReasons[reason] = discardReasons.getOrDefault(reason, 0) + 1
                }
                return@forEach
            }

            // Convert form factor string to enum, track unknown form factors
            val formFactor = FormFactor.fromValueOrNull(formFactorString!!)
            if (formFactor == null) {
                discardedCount++
                val reason = "Unknown form factor: $formFactorString"
                discardReasons[reason] = discardReasons.getOrDefault(reason, 0) + 1
                return@forEach
            }

            // If we reach here, all validations passed - create the device
            devices.add(AndroidDevice(
                brand = brand!!,
                device = device!!,
                manufacturer = manufacturer!!,
                modelName = modelName!!,
                ram = ram!!,
                formFactor = formFactor,
                processorName = processorName!!,
                gpu = gpu!!,
                screenSizes =
                    screenSizes!!
                        .split(CSV_MULTI_VALUE_SEPARATOR)
                        .filter { it.isNotEmpty() }
                        .toSortedSet()
                        .toList(),
                screenDensities =
                    screenDensities!!
                        .split(CSV_MULTI_VALUE_SEPARATOR)
                        .filter { it.isNotEmpty() }
                        .toSortedSet()
                        .toList()
                        .map { it.toInt() },
                abis =
                    abis!!
                        .split(CSV_MULTI_VALUE_SEPARATOR)
                        .filter { it.isNotEmpty() }
                        .toSortedSet()
                        .toList(),
                sdkVersions =
                    sdkVersions!!
                        .split(CSV_MULTI_VALUE_SEPARATOR)
                        .filter { it.isNotEmpty() }
                        .toSortedSet()
                        .toList()
                        .map { it.toInt() },
                openGlEsVersions =
                    openGlEsVersions!!
                        .split(CSV_MULTI_VALUE_SEPARATOR)
                        .filter { it.isNotEmpty() }
                        .toSortedSet()
                        .toList(),
            ))
        }
        
        return ParseResult(
            devices = devices,
            totalRows = deviceRows.size,
            discardedCount = discardedCount,
            discardReasons = discardReasons.toMap()
        )
    }
}
