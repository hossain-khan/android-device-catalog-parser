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
 * @see ParserConfig
 */
object Parser {
    private val csvReader: CsvReader = csvReader()

    /**
     * Helper function to get field value with config-based default handling.
     */
    private fun getFieldValue(
        value: String?,
        config: ParserConfig,
    ): String =
        when {
            value.isNullOrBlank() && config.useDefaultsForMissingFields -> config.defaultStringValue
            value.isNullOrBlank() -> ""
            else -> value
        }

    /**
     * Given Android Device Catalog CSV data, parses to list of [AndroidDevice].
     *
     * @param csvContent CSV content string with header row
     * @param config Optional parser configuration. Uses [ParserConfig.DEFAULT] if not provided
     * @return List of successfully parsed [AndroidDevice] objects
     */
    fun parseDeviceCatalogData(
        csvContent: String,
        config: ParserConfig = ParserConfig.DEFAULT,
    ): List<AndroidDevice> {
        val deviceRows: List<Map<String, String>> = csvReader.readAllWithHeader(csvContent)

        return deviceRows
            .mapNotNull { rowData ->
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

                // Apply configuration-based field handling
                val finalBrand = getFieldValue(brand, config)
                val finalDevice = getFieldValue(device, config)
                val finalManufacturer = getFieldValue(manufacturer, config)
                val finalModelName = getFieldValue(modelName, config)
                val finalRam = getFieldValue(ram, config)
                val finalFormFactorString = getFieldValue(formFactorString, config)
                val finalProcessorName = getFieldValue(processorName, config)
                val finalGpu = getFieldValue(gpu, config)
                val finalScreenSizes = getFieldValue(screenSizes, config)
                val finalScreenDensities = getFieldValue(screenDensities, config)
                val finalAbis = getFieldValue(abis, config)
                val finalSdkVersions = getFieldValue(sdkVersions, config)
                val finalOpenGlEsVersions = getFieldValue(openGlEsVersions, config)

                // If not using defaults and any required field is missing, skip this row
                if (!config.useDefaultsForMissingFields) {
                    if (finalBrand.isBlank() ||
                        finalDevice.isBlank() ||
                        finalManufacturer.isBlank() ||
                        finalModelName.isBlank() ||
                        finalRam.isBlank() ||
                        finalFormFactorString.isBlank() ||
                        finalProcessorName.isBlank() ||
                        finalGpu.isBlank() ||
                        finalScreenSizes.isBlank() ||
                        finalScreenDensities.isBlank() ||
                        finalAbis.isBlank() ||
                        finalSdkVersions.isBlank() ||
                        finalOpenGlEsVersions.isBlank()
                    ) {
                        return@mapNotNull null
                    }
                }

                // Convert form factor string to enum
                val formFactor =
                    FormFactor.fromValueOrNull(finalFormFactorString)
                        ?: config.defaultFormFactor
                if (formFactor == null) {
                    // No default form factor configured, skip record with unknown form factor
                    return@mapNotNull null
                }

                return@mapNotNull AndroidDevice(
                    brand = finalBrand,
                    device = finalDevice,
                    manufacturer = finalManufacturer,
                    modelName = finalModelName,
                    ram = finalRam,
                    formFactor = formFactor,
                    processorName = finalProcessorName,
                    gpu = finalGpu,
                    screenSizes =
                        finalScreenSizes
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    screenDensities =
                        finalScreenDensities
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .mapNotNull { it.toIntOrNull() ?: if (config.useDefaultsForMissingFields) config.defaultIntValue else null },
                    abis =
                        finalAbis
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    sdkVersions =
                        finalSdkVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .mapNotNull { it.toIntOrNull() ?: if (config.useDefaultsForMissingFields) config.defaultIntValue else null },
                    openGlEsVersions =
                        finalOpenGlEsVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                )
            }
    }

    /**
     * Enhanced version of [parseDeviceCatalogData] that provides detailed parsing statistics.
     *
     * Given Android Device Catalog CSV data, parses to [ParseResult] containing both
     * the successfully parsed devices and detailed information about discarded records.
     *
     * @param csvContent CSV content string with header row
     * @param config Optional parser configuration. Uses [ParserConfig.DEFAULT] if not provided
     * @return [ParseResult] containing parsed devices and parsing statistics
     */
    fun parseDeviceCatalogDataWithStats(
        csvContent: String,
        config: ParserConfig = ParserConfig.DEFAULT,
    ): ParseResult {
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

            // Track missing required fields when not using defaults
            if (!config.useDefaultsForMissingFields) {
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
            }

            // Apply configuration-based field handling
            val finalBrand = getFieldValue(brand, config)
            val finalDevice = getFieldValue(device, config)
            val finalManufacturer = getFieldValue(manufacturer, config)
            val finalModelName = getFieldValue(modelName, config)
            val finalRam = getFieldValue(ram, config)
            val finalFormFactorString = getFieldValue(formFactorString, config)
            val finalProcessorName = getFieldValue(processorName, config)
            val finalGpu = getFieldValue(gpu, config)
            val finalScreenSizes = getFieldValue(screenSizes, config)
            val finalScreenDensities = getFieldValue(screenDensities, config)
            val finalAbis = getFieldValue(abis, config)
            val finalSdkVersions = getFieldValue(sdkVersions, config)
            val finalOpenGlEsVersions = getFieldValue(openGlEsVersions, config)

            // Convert form factor string to enum, track unknown form factors
            val formFactor =
                FormFactor.fromValueOrNull(finalFormFactorString)
                    ?: config.defaultFormFactor
            if (formFactor == null) {
                discardedCount++
                val reason = "Unknown form factor: $finalFormFactorString"
                discardReasons[reason] = discardReasons.getOrDefault(reason, 0) + 1
                return@forEach
            }

            // If we reach here, all validations passed - create the device
            devices.add(
                AndroidDevice(
                    brand = finalBrand,
                    device = finalDevice,
                    manufacturer = finalManufacturer,
                    modelName = finalModelName,
                    ram = finalRam,
                    formFactor = formFactor,
                    processorName = finalProcessorName,
                    gpu = finalGpu,
                    screenSizes =
                        finalScreenSizes
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    screenDensities =
                        finalScreenDensities
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .mapNotNull { it.toIntOrNull() ?: if (config.useDefaultsForMissingFields) config.defaultIntValue else null },
                    abis =
                        finalAbis
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                    sdkVersions =
                        finalSdkVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList()
                            .mapNotNull { it.toIntOrNull() ?: if (config.useDefaultsForMissingFields) config.defaultIntValue else null },
                    openGlEsVersions =
                        finalOpenGlEsVersions
                            .split(CSV_MULTI_VALUE_SEPARATOR)
                            .filter { it.isNotEmpty() }
                            .toSortedSet()
                            .toList(),
                ),
            )
        }

        return ParseResult(
            devices = devices,
            totalRows = deviceRows.size,
            discardedCount = discardedCount,
            discardReasons = discardReasons.toMap(),
        )
    }
}
