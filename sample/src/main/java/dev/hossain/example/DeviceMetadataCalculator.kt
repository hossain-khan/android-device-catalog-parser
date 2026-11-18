package dev.hossain.example

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor

/**
 * Computes precomputed numeric metadata and sorting scores for [AndroidDevice] database records.
 */
object DeviceMetadataCalculator {
    private val ramRegex = Regex("""^\s*(\d+)(?:\s*-\s*(\d+))?\s*(MB|GB)\s*$""", RegexOption.IGNORE_CASE)

    private val majorOems =
        setOf(
            "google",
            "samsung",
            "motorola",
            "oneplus",
            "xiaomi",
            "oppo",
            "vivo",
            "sony",
            "nothing",
            "asus",
            "realme",
            "honor",
            "huawei",
            "lg",
            "lge",
            "lenovo",
            "nokia",
            "hmd global",
        )

    /**
     * Parses a RAM specification string (e.g. `"8000MB"`, `"12212-16415MB"`, `"4GB"`)
     * into a `(minMb, maxMb)` pair in megabytes. Returns `(0, 0)` if unparseable.
     */
    fun parseRamMb(ram: String): Pair<Int, Int> {
        val match = ramRegex.matchEntire(ram) ?: return 0 to 0
        val first = match.groupValues[1].toIntOrNull() ?: return 0 to 0
        val second = match.groupValues[2].toIntOrNull() ?: first
        val multiplier = if (match.groupValues[3].equals("GB", ignoreCase = true)) 1024 else 1

        val minMb = minOf(first, second) * multiplier
        val maxMb = maxOf(first, second) * multiplier
        return minMb to maxMb
    }

    /**
     * Computes a deterministic integer sort score combining:
     * - `maxSdk` (latest Android OS version)
     * - `formFactor == FormFactor.PHONE` priority
     * - `minSdk` (newer launch hardware)
     * - RAM tier based on `ramMaxMb`
     * - Major OEM tie-breaker
     */
    fun computeSortScore(
        device: AndroidDevice,
        minSdk: Int,
        maxSdk: Int,
        ramMaxMb: Int,
    ): Int {
        val maxSdkScore = maxSdk * 100_000
        val formFactorScore = if (device.formFactor == FormFactor.PHONE) 10_000 else 0
        val minSdkScore = minSdk * 100
        val ramTierScore =
            when {
                ramMaxMb >= 16_000 -> 50
                ramMaxMb >= 12_000 -> 40
                ramMaxMb >= 8_000 -> 30
                ramMaxMb >= 6_000 -> 20
                ramMaxMb >= 4_000 -> 10
                else -> 0
            }
        val oemScore = if (device.manufacturer.trim().lowercase() in majorOems) 5 else 0

        return maxSdkScore + formFactorScore + minSdkScore + ramTierScore + oemScore
    }
}
