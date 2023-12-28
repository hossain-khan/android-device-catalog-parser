package dev.hossain.android.catalogparser

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test cases for [DataSanitizer]
 */
class DataSanitizerTest {

    @Test
    fun `sanitizeDeviceRam returns input when no MB suffix present`() {
        val input = "3705"
        val expected = "3705"
        val actual = DataSanitizer.sanitizeDeviceRam(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `sanitizeDeviceRam returns max value when MB suffix present`() {
        val input = "3705-3735MB"
        val expected = "3735MB"
        val actual = DataSanitizer.sanitizeDeviceRam(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `sanitizeDeviceRam returns input when no numbers present`() {
        val input = "NoNumbersMB"
        val expected = "NoNumbersMB"
        val actual = DataSanitizer.sanitizeDeviceRam(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `extractNumbers returns empty list when no numbers present`() {
        val input = "NoNumbers"
        val expected = emptyList<Int>()
        val actual = DataSanitizer.extractNumbers(input)
        assertEquals(expected, actual)
    }

    @Test
    fun `extractNumbers returns list of numbers when numbers present`() {
        val input = "3705-3735-3829"
        val expected = listOf(3705, 3735, 3829)
        val actual = DataSanitizer.extractNumbers(input)
        assertEquals(expected, actual)
    }
}