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

        return deviceRows.map { rowData ->
            val brand = rowData[CSV_KEY_BRAND]
            val device = rowData[CSV_KEY_DEVICE]
            val manufacturer = rowData[CSV_KEY_MANUFACTURER]
            val modelName = rowData[CSV_KEY_MODEL_NAME]
            val ram = rowData[CSV_KEY_RAM]
            val formFactor = rowData[CSV_KEY_FORM_FACTOR]
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
                formFactor.isNullOrBlank() ||
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

            return@map AndroidDevice(
                brand = brand,
                device = device,
                manufacturer = manufacturer,
                modelName = modelName,
                ram = ram,
                formFactor = formFactor,
                processorName = processorName,
                gpu = gpu,
                screenSizes = screenSizes.split(CSV_MULTI_VALUE_SEPARATOR)
                    .filter { it.isNotEmpty() }
                    .toSortedSet()
                    .toList(),
                screenDensities = screenDensities.split(CSV_MULTI_VALUE_SEPARATOR)
                    .filter { it.isNotEmpty() }
                    .toSortedSet()
                    .toList()
                    .map { it.toInt() },
                abis = abis.split(CSV_MULTI_VALUE_SEPARATOR)
                    .filter { it.isNotEmpty() }
                    .toSortedSet()
                    .toList(),
                sdkVersions = sdkVersions.split(CSV_MULTI_VALUE_SEPARATOR)
                    .filter { it.isNotEmpty() }
                    .toSortedSet()
                    .toList()
                    .map { it.toInt() },
                openGlEsVersions = openGlEsVersions.split(CSV_MULTI_VALUE_SEPARATOR)
                    .filter { it.isNotEmpty() }
                    .toSortedSet()
                    .toList()
            )
        }.filterNotNull()
    }
}