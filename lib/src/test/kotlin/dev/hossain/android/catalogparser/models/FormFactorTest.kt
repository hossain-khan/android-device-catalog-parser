package dev.hossain.android.catalogparser.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Test cases for [FormFactor] enum
 */
class FormFactorTest {
    @Test
    fun `fromCsvValue returns correct FormFactor for valid CSV values`() {
        assertEquals(FormFactor.PHONE, FormFactor.fromCsvValue("Phone"))
        assertEquals(FormFactor.TABLET, FormFactor.fromCsvValue("Tablet"))
        assertEquals(FormFactor.TV, FormFactor.fromCsvValue("TV"))
        assertEquals(FormFactor.WEARABLE, FormFactor.fromCsvValue("Wearable"))
        assertEquals(FormFactor.ANDROID_AUTOMOTIVE, FormFactor.fromCsvValue("Android Automotive"))
        assertEquals(FormFactor.CHROMEBOOK, FormFactor.fromCsvValue("Chromebook"))
        assertEquals(FormFactor.GOOGLE_PLAY_GAMES_ON_PC, FormFactor.fromCsvValue("Google Play Games on PC"))
    }

    @Test
    fun `fromCsvValue throws IllegalArgumentException for invalid CSV value`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                FormFactor.fromCsvValue("Invalid Form Factor")
            }
        assertEquals("Unknown form factor: Invalid Form Factor", exception.message)
    }

    @Test
    fun `fromCsvValueOrNull returns correct FormFactor for valid CSV values`() {
        assertEquals(FormFactor.PHONE, FormFactor.fromCsvValueOrNull("Phone"))
        assertEquals(FormFactor.TABLET, FormFactor.fromCsvValueOrNull("Tablet"))
        assertEquals(FormFactor.TV, FormFactor.fromCsvValueOrNull("TV"))
        assertEquals(FormFactor.WEARABLE, FormFactor.fromCsvValueOrNull("Wearable"))
        assertEquals(FormFactor.ANDROID_AUTOMOTIVE, FormFactor.fromCsvValueOrNull("Android Automotive"))
        assertEquals(FormFactor.CHROMEBOOK, FormFactor.fromCsvValueOrNull("Chromebook"))
        assertEquals(FormFactor.GOOGLE_PLAY_GAMES_ON_PC, FormFactor.fromCsvValueOrNull("Google Play Games on PC"))
    }

    @Test
    fun `fromCsvValueOrNull returns null for invalid CSV value`() {
        assertNull(FormFactor.fromCsvValueOrNull("Invalid Form Factor"))
        assertNull(FormFactor.fromCsvValueOrNull(""))
        assertNull(FormFactor.fromCsvValueOrNull("phone")) // case sensitive
    }

    @Test
    fun `getAllCsvValues returns all CSV values`() {
        val expectedValues =
            listOf(
                "Phone",
                "Tablet",
                "TV",
                "Wearable",
                "Android Automotive",
                "Chromebook",
                "Google Play Games on PC",
            )
        assertEquals(expectedValues, FormFactor.getAllCsvValues())
    }

    @Test
    fun `enum values have correct csvValue and description`() {
        assertEquals("Phone", FormFactor.PHONE.csvValue)
        assertEquals("Smartphones and mobile phones", FormFactor.PHONE.description)

        assertEquals("Tablet", FormFactor.TABLET.csvValue)
        assertEquals("Tablet devices with larger screens", FormFactor.TABLET.description)

        assertEquals("TV", FormFactor.TV.csvValue)
        assertEquals("Television and Android TV devices", FormFactor.TV.description)

        assertEquals("Wearable", FormFactor.WEARABLE.csvValue)
        assertEquals("Smartwatches and other wearable devices", FormFactor.WEARABLE.description)

        assertEquals("Android Automotive", FormFactor.ANDROID_AUTOMOTIVE.csvValue)
        assertEquals("In-vehicle Android Automotive systems", FormFactor.ANDROID_AUTOMOTIVE.description)

        assertEquals("Chromebook", FormFactor.CHROMEBOOK.csvValue)
        assertEquals("Chromebook devices running Android apps", FormFactor.CHROMEBOOK.description)

        assertEquals("Google Play Games on PC", FormFactor.GOOGLE_PLAY_GAMES_ON_PC.csvValue)
        assertEquals("PC platform for Android games", FormFactor.GOOGLE_PLAY_GAMES_ON_PC.description)
    }

    @Test
    fun `all form factors are covered in tests`() {
        // This test ensures we don't forget to add tests when new form factors are added
        val testedFormFactors =
            setOf(
                FormFactor.PHONE,
                FormFactor.TABLET,
                FormFactor.TV,
                FormFactor.WEARABLE,
                FormFactor.ANDROID_AUTOMOTIVE,
                FormFactor.CHROMEBOOK,
                FormFactor.GOOGLE_PLAY_GAMES_ON_PC,
            )

        val allFormFactors = FormFactor.values().toSet()
        assertEquals(allFormFactors, testedFormFactors, "All FormFactor enum values should be tested")
    }

    @Test
    fun `CSV values are unique`() {
        val csvValues = FormFactor.getAllCsvValues()
        val uniqueCsvValues = csvValues.toSet()
        assertEquals(csvValues.size, uniqueCsvValues.size, "All CSV values should be unique")
    }

    @Test
    fun `enum names match expected pattern`() {
        // Verify that enum names follow the expected naming convention
        assertEquals("PHONE", FormFactor.PHONE.name)
        assertEquals("TABLET", FormFactor.TABLET.name)
        assertEquals("TV", FormFactor.TV.name)
        assertEquals("WEARABLE", FormFactor.WEARABLE.name)
        assertEquals("ANDROID_AUTOMOTIVE", FormFactor.ANDROID_AUTOMOTIVE.name)
        assertEquals("CHROMEBOOK", FormFactor.CHROMEBOOK.name)
        assertEquals("GOOGLE_PLAY_GAMES_ON_PC", FormFactor.GOOGLE_PLAY_GAMES_ON_PC.name)
    }
}
