package dev.hossain.android.catalogparser.models

/**
 * Result of parsing CSV data with detailed statistics about the parsing process.
 *
 * @param devices List of successfully parsed [AndroidDevice] objects
 * @param totalRows Total number of data rows processed (excluding header)
 * @param discardedCount Number of rows that were discarded during parsing
 * @param discardReasons Map of discard reasons to their occurrence counts
 */
data class ParseResult(
    /**
     * List of successfully parsed Android devices.
     */
    val devices: List<AndroidDevice>,
    /**
     * Total number of data rows processed from CSV (excluding header row).
     */
    val totalRows: Int,
    /**
     * Number of rows that were discarded during parsing due to validation failures.
     */
    val discardedCount: Int,
    /**
     * Map containing the reasons for discarding rows and their occurrence counts.
     * Common reasons include:
     * - "Missing required field: <field_name>" - when a required field is null or blank
     * - "Unknown form factor: <value>" - when form factor cannot be parsed
     */
    val discardReasons: Map<String, Int>,
) {
    /**
     * Number of successfully parsed devices.
     */
    val successfulCount: Int get() = devices.size

    /**
     * Percentage of successfully parsed rows (0.0 to 100.0).
     */
    val successRate: Double get() = if (totalRows == 0) 0.0 else (successfulCount.toDouble() / totalRows) * 100.0
}
