package dev.hossain.android.catalogparser

object DataSanitizer {
    private const val SUFFIX_MB = "MB"

    /**
     * Given range of RAM value like `3705-3735MB`, takes the maximum ram value to keep it simple.
     */
    fun sanitizeDeviceRam(ram: String): String {
        if (!ram.contains(SUFFIX_MB)) {
            return ram
        }
        val maxRamRangeValue = extractNumbers(ram).maxOrNull()
        if (maxRamRangeValue == null) {
            return ram
        } else {
            return "${maxRamRangeValue}${SUFFIX_MB}"
        }
    }

    /**
     * Extracts all number from range of numbers like: `3705-3735-3829`
     */
    internal fun extractNumbers(input: String): List<Int> {
        val regex = Regex("""\d+""")
        val matches = regex.findAll(input)
        val numbersList = matches.map { it.value }.toList().map { it.toInt() }

        return numbersList
    }
}
