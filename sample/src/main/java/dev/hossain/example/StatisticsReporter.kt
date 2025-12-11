package dev.hossain.example

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.ParseResult

/**
 * Handles reporting and printing of device statistics.
 */
class StatisticsReporter {
    /**
     * Prints basic parsing statistics.
     *
     * @param parseResult The result from parsing with statistics
     */
    fun printParsingStatistics(parseResult: ParseResult) {
        println("\n=== Parsing Statistics ===")
        println("Total rows processed: ${parseResult.totalRows}")
        println("Successfully parsed: ${parseResult.successfulCount}")
        println("Discarded: ${parseResult.discardedCount}")
        println("Success rate: ${"%.2f".format(parseResult.successRate)}%")

        if (parseResult.discardReasons.isNotEmpty()) {
            println("\nTop discard reasons:")
            parseResult.discardReasons
                .toList()
                .sortedByDescending { it.second }
                .take(10)
                .forEach { (reason, count) ->
                    println("  • $reason: $count")
                }
        }
        println("=========================\n")
    }

    /**
     * Prints a summary of device catalog data.
     *
     * @param devices List of parsed devices
     */
    fun printDeviceSummary(devices: List<AndroidDevice>) {
        println("\n=== Device Catalog Summary ===")
        println("Total devices: ${devices.size}")

        if (devices.isEmpty()) {
            println("No devices to summarize.")
            println("==============================\n")
            return
        }

        // Form factor distribution
        val formFactorCounts = devices.groupingBy { it.formFactor }.eachCount()
        println("\nForm Factor Distribution:")
        formFactorCounts
            .entries
            .sortedByDescending { it.value }
            .forEach { (formFactor, count) ->
                val percentage = (count * 100.0 / devices.size)
                println("  • ${formFactor.value}: $count (${"%.1f".format(percentage)}%)")
            }

        println("\nUnique Attributes:")
        println("  • Processors: ${devices.map { it.processorName }.toSet().size}")
        println("  • GPUs: ${devices.map { it.gpu }.toSet().size}")
        println("  • Screen sizes: ${devices.flatMap { it.screenSizes }.toSet().size}")
        println("  • ABIs: ${devices.flatMap { it.abis }.toSet().size}")
        println("  • SDK versions: ${devices.flatMap { it.sdkVersions }.toSet().size}")
        println("  • OpenGL ES versions: ${devices.flatMap { it.openGlEsVersions }.toSet().size}")
        println("  • Screen densities: ${devices.flatMap { it.screenDensities }.toSet().size}")
        println("==============================\n")
    }

    /**
     * Prints detailed attribute lists (for debugging/exploration).
     * This is verbose and should only be called when detailed info is needed.
     *
     * @param devices List of parsed devices
     */
    fun printDetailedAttributes(devices: List<AndroidDevice>) {
        println("\n=== Detailed Device Attributes ===\n")

        // Unique processors
        val uniqueProcessors = devices.map { it.processorName }.toSet().sorted()
        println("Unique Processors (${uniqueProcessors.size}):")
        uniqueProcessors.take(10).forEach { println("  • $it") }
        if (uniqueProcessors.size > 10) {
            println("  ... and ${uniqueProcessors.size - 10} more")
        }

        // Unique GPUs
        val uniqueGpus = devices.map { it.gpu }.toSet().sorted()
        println("\nUnique GPUs (${uniqueGpus.size}):")
        uniqueGpus.take(10).forEach { println("  • $it") }
        if (uniqueGpus.size > 10) {
            println("  ... and ${uniqueGpus.size - 10} more")
        }

        // Screen size ranges
        val uniqueScreenSizes = devices.flatMap { it.screenSizes }.toSet()
        val sortedScreenSizes =
            uniqueScreenSizes.sortedWith(
                compareBy(
                    { it.substringBefore('x').toIntOrNull() ?: 0 },
                    { it.substringAfter('x').toIntOrNull() ?: 0 },
                ),
            )
        println("\nScreen Size Range (${sortedScreenSizes.size} unique):")
        when {
            sortedScreenSizes.isEmpty() -> println("  • No screen sizes available")
            sortedScreenSizes.size == 1 -> println("  • ${sortedScreenSizes.first()}")
            else -> {
                println("  • Smallest: ${sortedScreenSizes.first()}")
                println("  • Largest: ${sortedScreenSizes.last()}")
            }
        }

        // ABIs
        val uniqueAbis = devices.flatMap { it.abis }.toSet().sorted()
        println("\nSupported ABIs:")
        uniqueAbis.forEach { println("  • $it") }

        // SDK versions
        val uniqueSdkVersions = devices.flatMap { it.sdkVersions }.toSet().sorted()
        println("\nSDK Versions:")
        println("  • Range: ${uniqueSdkVersions.first()} - ${uniqueSdkVersions.last()}")
        println("  • Total: ${uniqueSdkVersions.size} versions")

        // OpenGL ES versions
        val uniqueOpenGlEsVersions = devices.flatMap { it.openGlEsVersions }.toSet().sorted()
        println("\nOpenGL ES Versions:")
        uniqueOpenGlEsVersions.forEach { println("  • $it") }

        // Screen densities
        val uniqueScreenDensities = devices.flatMap { it.screenDensities }.toSet().sorted()
        println("\nScreen Densities:")
        println("  • Range: ${uniqueScreenDensities.first()} - ${uniqueScreenDensities.last()} dpi")
        println("  • Total: ${uniqueScreenDensities.size} unique values")

        println("\n===================================\n")
    }

    /**
     * Prints parser configuration comparison results.
     *
     * @param configName Name of the configuration being tested
     * @param parseResult The parse result with the configuration
     */
    fun printConfigurationResult(
        configName: String,
        parseResult: ParseResult,
    ) {
        println("Configuration: $configName")
        println("  • Successfully parsed: ${parseResult.successfulCount}")
        println("  • Success rate: ${"%.2f".format(parseResult.successRate)}%")
        if (parseResult.discardedCount > 0) {
            println("  • Discarded: ${parseResult.discardedCount}")
        }
    }
}
