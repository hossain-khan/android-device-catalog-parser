package dev.hossain.example

import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.ParserConfig
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.ParseResult

/**
 * Handles parsing of Android device catalog CSV data.
 */
class DeviceCatalogParser {
    private val parser = Parser

    /**
     * Parses device catalog from CSV content.
     *
     * @param csvContent The CSV file content as string
     * @return List of parsed AndroidDevice objects
     */
    fun parseDevices(csvContent: String): List<AndroidDevice> = parser.parseDeviceCatalogData(csvContent)

    /**
     * Parses device catalog from CSV content with enhanced statistics.
     *
     * @param csvContent The CSV file content as string
     * @param config Optional parser configuration
     * @return ParseResult containing devices and statistics
     */
    fun parseDevicesWithStats(
        csvContent: String,
        config: ParserConfig? = null,
    ): ParseResult =
        if (config != null) {
            parser.parseDeviceCatalogDataWithStats(csvContent, config)
        } else {
            parser.parseDeviceCatalogDataWithStats(csvContent)
        }

    /**
     * Loads CSV content from resources.
     *
     * @param resourcePath Path to the CSV file in resources
     * @return CSV content as string
     */
    fun loadCsvFromResources(resourcePath: String): String =
        object {}
            .javaClass
            .getResourceAsStream(resourcePath)!!
            .bufferedReader()
            .readText()
}
