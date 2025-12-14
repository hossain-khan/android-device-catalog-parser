package dev.hossain.example

import dev.hossain.android.catalogparser.models.AndroidDevice

/**
 * Data Summary Generator for Android Device Catalog.
 *
 * This class generates comprehensive markdown summaries of the Android device catalog data.
 * It processes parsed device data to create the DATA_SUMMARY.md file, which includes:
 * - Total device counts and form factor distributions
 * - Complete lists of unique processors, GPUs, screen sizes, ABIs, SDK versions, etc.
 * - Statistical summaries and ranges for various device attributes
 *
 * Usage: Call [generateSummary] with a list of parsed [AndroidDevice] objects to generate
 * the complete markdown content for DATA_SUMMARY.md.
 *
 * @see AndroidDevice
 */
class DataSummaryGenerator {
    /**
     * Generates a complete data summary markdown content.
     */
    fun generateSummary(devices: List<AndroidDevice>): String {
        val sb = StringBuilder()

        // Header
        sb.appendLine(
            "The data summarizes information from a catalog of devices, specifically **${devices.size} devices** were parsed from a CSV file.",
        )
        sb.appendLine()

        // Form factors
        val formFactorCounts = devices.groupingBy { it.formFactor }.eachCount()
        sb.appendLine("The devices are categorized into several form factors:")
        formFactorCounts
            .entries
            .sortedByDescending { it.value }
            .forEach { (formFactor, count) ->
                // Handle pluralization properly
                val label =
                    when (formFactor.value) {
                        "Phone" -> "Phones"
                        "Tablet" -> "Tablets"
                        "TV" -> "TVs"
                        "Wearable" -> "Wearables"
                        "Chromebook" -> "Chromebooks"
                        "Android Automotive" -> "Android Automotive"
                        "Google Play Games on PC" -> "Google Play Games on PC"
                        else -> "${formFactor.value}s"
                    }
                val deviceWord = if (count == 1) "device" else "devices"
                sb.appendLine("*   **$label**: ${"%,d".format(count)} $deviceWord")
            }
        sb.appendLine()

        // Processors
        val uniqueProcessors = devices.map { it.processorName }.toSet().sorted()
        sb.appendLine("The catalog includes a wide variety of components and specifications:")
        sb.appendLine("*   **Unique Processors**: ${uniqueProcessors.size}")
        sb.appendLine()

        // GPUs
        val uniqueGpus = devices.map { it.gpu }.toSet().sorted()
        sb.appendLine("*   **Unique GPUs**: ${uniqueGpus.size}")
        sb.appendLine()

        // Screen sizes
        val uniqueScreenSizes = devices.flatMap { it.screenSizes }.toSet()
        val sortedScreenSizes =
            uniqueScreenSizes.sortedWith(
                compareBy(
                    { it.substringBefore('x').toIntOrNull() ?: 0 },
                    { it.substringAfter('x').toIntOrNull() ?: 0 },
                ),
            )
        sb.appendLine(
            "*   **Unique Screen Sizes**: The catalog contains ${sortedScreenSizes.size} unique screen resolutions, ranging from small (e.g., ${sortedScreenSizes.first()}) to very large (e.g., ${sortedScreenSizes.last()}).",
        )
        sb.appendLine()

        // ABIs
        val uniqueAbis = devices.flatMap { it.abis }.toSet().sorted()
        sb.appendLine(
            "*   **Unique ABIs (Application Binary Interfaces)**: The supported ABIs are ${uniqueAbis.joinToString(", ") { "\"$it\"" }}.",
        )
        sb.appendLine()

        // SDK versions
        val uniqueSdkVersions = devices.flatMap { it.sdkVersions }.toSet().sorted()
        sb.appendLine(
            "*   **Unique SDK Versions**: The devices support Android SDK versions from **${uniqueSdkVersions.first()} to ${uniqueSdkVersions.last()}**.",
        )
        sb.appendLine()

        // OpenGL ES versions
        val uniqueOpenGlEsVersions = devices.flatMap { it.openGlEsVersions }.toSet().sorted()
        sb.appendLine(
            "*   **Unique OpenGL ES Versions**: The graphics API versions include ${uniqueOpenGlEsVersions.joinToString(
                ", ",
            ) { "\"$it\"" }}.",
        )
        sb.appendLine()

        // Screen densities
        val uniqueScreenDensities = devices.flatMap { it.screenDensities }.toSet().sorted()
        sb.appendLine(
            "*   **Unique Screen Densities**: A wide range of screen densities are present, from **${uniqueScreenDensities.first()} to ${uniqueScreenDensities.last()}**.",
        )
        sb.appendLine()

        sb.appendLine(
            "In essence, the data provides a detailed snapshot of the hardware and software diversity within the parsed device catalog, encompassing a wide range of form factors, processing units, graphics capabilities, display specifications, and software compatibility.",
        )
        sb.appendLine()
        sb.appendLine()

        // Raw statistics section
        sb.appendLine("```")
        sb.appendLine("Parsed ${devices.size} devices from the catalog CSV file.")
        sb.appendLine()

        sb.appendLine("**Form factor counts:**")
        formFactorCounts
            .entries
            .sortedByDescending { it.value }
            .forEach { (formFactor, count) ->
                sb.appendLine("\"${formFactor.value}\": $count")
            }
        sb.appendLine()
        sb.appendLine()

        // Processors list
        sb.appendLine("**Unique processors:**")
        uniqueProcessors.forEach { processor ->
            sb.appendLine("\"$processor\"")
        }
        sb.appendLine()
        sb.appendLine()

        // GPUs list
        sb.appendLine("**Unique GPUs:**")
        uniqueGpus.forEach { gpu ->
            sb.appendLine("\"$gpu\"")
        }
        sb.appendLine()
        sb.appendLine()

        // Screen sizes list
        sb.appendLine("**Unique screen sizes:**")
        sortedScreenSizes.forEach { size ->
            sb.appendLine("\"$size\"")
        }
        sb.appendLine()
        sb.appendLine()

        // ABIs list
        sb.appendLine("**Unique ABIs:**")
        uniqueAbis.forEach { abi ->
            sb.appendLine("\"$abi\"")
        }
        sb.appendLine()
        sb.appendLine()

        // SDK versions list
        sb.appendLine("**Unique SDK versions:**")
        uniqueSdkVersions.forEach { sdk ->
            sb.appendLine("\"$sdk\"")
        }
        sb.appendLine()
        sb.appendLine()

        // OpenGL ES versions list
        sb.appendLine("**Unique OpenGL ES versions:**")
        uniqueOpenGlEsVersions.forEach { opengl ->
            sb.appendLine("\"$opengl\"")
        }
        sb.appendLine()
        sb.appendLine()

        // Screen densities list
        sb.appendLine("**Unique screen densities:**")
        uniqueScreenDensities.forEach { density ->
            sb.appendLine("\"$density\"")
        }
        sb.appendLine("```")

        return sb.toString()
    }
}
