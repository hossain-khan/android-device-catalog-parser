package dev.hossain.android.catalogparser

import dev.hossain.android.catalogparser.models.FormFactor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests the [ParserConfig] builder pattern and configuration options.
 */
class ParserConfigTest {
    @Test
    fun `default config should have expected defaults`() {
        val config = ParserConfig.DEFAULT

        assertEquals(false, config.useDefaultsForMissingFields)
        assertEquals("", config.defaultStringValue)
        assertEquals(0, config.defaultIntValue)
        assertNull(config.defaultFormFactor)
    }

    @Test
    fun `builder should create config with default values`() {
        val config = ParserConfig.builder().build()

        assertEquals(false, config.useDefaultsForMissingFields)
        assertEquals("", config.defaultStringValue)
        assertEquals(0, config.defaultIntValue)
        assertNull(config.defaultFormFactor)
    }

    @Test
    fun `builder should allow setting useDefaultsForMissingFields`() {
        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .build()

        assertEquals(true, config.useDefaultsForMissingFields)
    }

    @Test
    fun `builder should allow setting defaultStringValue`() {
        val config =
            ParserConfig
                .builder()
                .defaultStringValue("Unknown")
                .build()

        assertEquals("Unknown", config.defaultStringValue)
    }

    @Test
    fun `builder should allow setting defaultIntValue`() {
        val config =
            ParserConfig
                .builder()
                .defaultIntValue(999)
                .build()

        assertEquals(999, config.defaultIntValue)
    }

    @Test
    fun `builder should allow setting defaultFormFactor`() {
        val config =
            ParserConfig
                .builder()
                .defaultFormFactor(FormFactor.PHONE)
                .build()

        assertEquals(FormFactor.PHONE, config.defaultFormFactor)
    }

    @Test
    fun `builder should allow fluent chaining`() {
        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(true)
                .defaultStringValue("N/A")
                .defaultIntValue(0)
                .defaultFormFactor(FormFactor.TABLET)
                .build()

        assertEquals(true, config.useDefaultsForMissingFields)
        assertEquals("N/A", config.defaultStringValue)
        assertEquals(0, config.defaultIntValue)
        assertEquals(FormFactor.TABLET, config.defaultFormFactor)
    }

    @Test
    fun `builder with useDefaultsForMissingFields should handle false parameter`() {
        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields(false)
                .build()

        assertEquals(false, config.useDefaultsForMissingFields)
    }

    @Test
    fun `builder with useDefaultsForMissingFields no parameter should default to true`() {
        val config =
            ParserConfig
                .builder()
                .useDefaultsForMissingFields()
                .build()

        assertEquals(true, config.useDefaultsForMissingFields)
    }
}
