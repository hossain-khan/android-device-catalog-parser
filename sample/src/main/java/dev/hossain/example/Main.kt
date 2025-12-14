package dev.hossain.example

import dev.hossain.android.catalogparser.ParserConfig
import dev.hossain.android.catalogparser.models.FormFactor
import java.io.File
import java.util.Date

/**
 * Main application entry point.
 *
 * Demonstrates the Android Device Catalog Parser library capabilities:
 * - Parsing CSV device catalog data
 * - Generating statistics and reports
 * - Exporting data to JSON format
 * - Parser configuration options
 */
fun main() {
    val title = "Android Device Catalog Parser - Sample App"
    val timestamp = "Run on: ${Date()}"
    val boxWidth = maxOf(title.length, timestamp.length) + 4
    val horizontalBorder = "═".repeat(boxWidth)

    println("╔$horizontalBorder╗")
    println("║  ${title.padEnd(boxWidth - 2)}║")
    println("║  ${timestamp.padEnd(boxWidth - 2)}║")
    println("╚$horizontalBorder╝\n")

    // Initialize components
    val parser = DeviceCatalogParser()
    val reporter = StatisticsReporter()
    val exporter = JsonExporter()

    // Load and parse device catalog
    println("Loading device catalog from CSV...")
    val csvContent = parser.loadCsvFromResources("/android-devices-catalog.csv")
    val devices = parser.parseDevices(csvContent)
    println("✓ Loaded and parsed ${devices.size} devices\n")

    // Display comprehensive statistics
    val parseResult = parser.parseDevicesWithStats(csvContent)
    reporter.printParsingStatistics(parseResult)
    reporter.printDeviceSummary(devices)

    // Demonstrate parser configuration options
    demonstrateParserConfigurations(parser, reporter, csvContent)

    // Export data to JSON files
    println("\n=== Exporting Data ===")
    exporter.writeDeviceListToJson(
        deviceList = devices,
        filePath = "sample/src/main/resources/android-devices-catalog.json",
    )
    exporter.validateJsonWithSchema(
        jsonPath = "sample/src/main/resources/android-devices-catalog.json",
        schemaPath = "sample/src/main/resources/android-devices-catalog-schema.json",
    )

    // Export unfiltered data (with custom config)
    val customConfig =
        ParserConfig
            .builder()
            .useDefaultsForMissingFields(true)
            .defaultStringValue("Unknown")
            .defaultIntValue(0)
            .defaultFormFactor(FormFactor.UNKNOWN)
            .build()
    val unfilteredResult = parser.parseDevicesWithStats(csvContent, customConfig)
    exporter.writeDeviceListToJson(
        deviceList = unfilteredResult.devices,
        filePath = "sample/src/main/resources/android-devices-catalog-unfiltered.json",
    )
    exporter.validateJsonWithSchema(
        jsonPath = "sample/src/main/resources/android-devices-catalog-unfiltered.json",
        schemaPath = "sample/src/main/resources/android-devices-catalog-schema.json",
    )
    println("======================\n")

    // Reminder for minification
    println("⚠️  REMINDER: Run './minify-json.sh' to create minified JSON versions\n")

    // Generate DATA_SUMMARY.md
    println("=== Generating DATA_SUMMARY.md ===")
    val summaryGenerator = DataSummaryGenerator()
    val summaryContent = summaryGenerator.generateSummary(devices)
    File("DATA_SUMMARY.md").writeText(summaryContent)
    println("✓ Generated DATA_SUMMARY.md")
    println("==================================\n")

    println("✓ Sample application completed successfully!")
}

/**
 * Demonstrates different parser configuration options and their effects.
 */
private fun demonstrateParserConfigurations(
    parser: DeviceCatalogParser,
    reporter: StatisticsReporter,
    csvContent: String,
) {
    println("\n=== Parser Configuration Examples ===\n")

    // Config 1: Use defaults for missing fields
    val configWithDefaults =
        ParserConfig
            .builder()
            .useDefaultsForMissingFields(true)
            .defaultStringValue("Unknown")
            .defaultFormFactor(FormFactor.PHONE)
            .build()

    val resultWithDefaults = parser.parseDevicesWithStats(csvContent, configWithDefaults)
    reporter.printConfigurationResult("Defaults for missing fields", resultWithDefaults)

    // Config 2: Custom defaults
    val customConfig =
        ParserConfig
            .builder()
            .useDefaultsForMissingFields(true)
            .defaultStringValue("Unknown")
            .defaultIntValue(0)
            .defaultFormFactor(FormFactor.UNKNOWN)
            .build()

    val customResult = parser.parseDevicesWithStats(csvContent, customConfig)
    reporter.printConfigurationResult("\nCustom defaults (Unknown/0/UNKNOWN)", customResult)

    println("\n======================================\n")
}
