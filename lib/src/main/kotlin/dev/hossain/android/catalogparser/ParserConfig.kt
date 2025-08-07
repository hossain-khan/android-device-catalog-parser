package dev.hossain.android.catalogparser

import dev.hossain.android.catalogparser.models.FormFactor

/**
 * Configuration for the Android Device Catalog Parser.
 *
 * This class controls how the parser handles missing or invalid data during CSV parsing.
 * By default, the parser discards rows with missing required fields or unknown form factors.
 * With configuration, you can choose to use default values instead.
 *
 * @param useDefaultsForMissingFields Whether to use default values instead of discarding rows with missing fields
 * @param defaultStringValue Default value to use for missing string fields when [useDefaultsForMissingFields] is true
 * @param defaultIntValue Default value to use for missing integer fields when [useDefaultsForMissingFields] is true
 * @param defaultFormFactor Default form factor to use for unknown form factors. If null, unknown form factors are still discarded
 */
data class ParserConfig(
    val useDefaultsForMissingFields: Boolean = false,
    val defaultStringValue: String = "",
    val defaultIntValue: Int = 0,
    val defaultFormFactor: FormFactor? = null,
) {
    companion object {
        /**
         * Default configuration that maintains backward compatibility.
         * Discards rows with missing fields or unknown form factors.
         */
        val DEFAULT = ParserConfig()

        /**
         * Creates a new ParserConfig.Builder for fluent configuration.
         */
        fun builder(): Builder = Builder()
    }

    /**
     * Builder class for creating ParserConfig instances with a fluent API.
     */
    class Builder {
        private var useDefaultsForMissingFields: Boolean = false
        private var defaultStringValue: String = ""
        private var defaultIntValue: Int = 0
        private var defaultFormFactor: FormFactor? = null

        /**
         * Configure the parser to use default values instead of discarding rows with missing fields.
         *
         * @param enabled Whether to use defaults for missing fields
         * @return This builder for method chaining
         */
        fun useDefaultsForMissingFields(enabled: Boolean = true): Builder {
            this.useDefaultsForMissingFields = enabled
            return this
        }

        /**
         * Set the default string value to use for missing string fields.
         * Only used when [useDefaultsForMissingFields] is enabled.
         *
         * @param value Default string value (e.g., "Unknown", "N/A", "")
         * @return This builder for method chaining
         */
        fun defaultStringValue(value: String): Builder {
            this.defaultStringValue = value
            return this
        }

        /**
         * Set the default integer value to use for missing integer fields.
         * Only used when [useDefaultsForMissingFields] is enabled.
         *
         * @param value Default integer value
         * @return This builder for method chaining
         */
        fun defaultIntValue(value: Int): Builder {
            this.defaultIntValue = value
            return this
        }

        /**
         * Set the default form factor to use for unknown form factors.
         * If not set (null), unknown form factors will still be discarded.
         *
         * @param formFactor Default form factor to use for unknown values
         * @return This builder for method chaining
         */
        fun defaultFormFactor(formFactor: FormFactor): Builder {
            this.defaultFormFactor = formFactor
            return this
        }

        /**
         * Build the ParserConfig instance.
         *
         * @return Configured ParserConfig instance
         */
        fun build(): ParserConfig =
            ParserConfig(
                useDefaultsForMissingFields = useDefaultsForMissingFields,
                defaultStringValue = defaultStringValue,
                defaultIntValue = defaultIntValue,
                defaultFormFactor = defaultFormFactor,
            )
    }
}
