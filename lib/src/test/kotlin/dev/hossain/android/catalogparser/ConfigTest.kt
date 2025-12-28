package dev.hossain.android.catalogparser

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [Config] object to ensure constant values are not changed accidentally.
 */
class ConfigTest {

    @Test
    fun `constants should have expected values`() {
        assertEquals(";", Config.CSV_MULTI_VALUE_SEPARATOR)
        assertEquals("Brand", Config.CSV_KEY_BRAND)
        assertEquals("Device", Config.CSV_KEY_DEVICE)
        assertEquals("Manufacturer", Config.CSV_KEY_MANUFACTURER)
        assertEquals("Model Name", Config.CSV_KEY_MODEL_NAME)
        assertEquals("RAM (TotalMem)", Config.CSV_KEY_RAM)
        assertEquals("Form Factor", Config.CSV_KEY_FORM_FACTOR)
        assertEquals("System on Chip", Config.CSV_KEY_SOC)
        assertEquals("GPU", Config.CSV_KEY_SCREEN_GPU)
        assertEquals("Screen Sizes", Config.CSV_KEY_SCREEN_SIZES)
        assertEquals("Screen Densities", Config.CSV_KEY_SCREEN_DENSITIES)
        assertEquals("ABIs", Config.CSV_KEY_ABIS)
        assertEquals("Android SDK Versions", Config.CSV_KEY_SDK_VERSIONS)
        assertEquals("OpenGL ES Versions", Config.CSV_KEY_OPENGL_ES_VERSIONS)
    }
}
