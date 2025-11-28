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
    fun `fromValue returns correct FormFactor for valid values`() {
        assertEquals(FormFactor.PHONE, FormFactor.fromValue("Phone"))
        assertEquals(FormFactor.TABLET, FormFactor.fromValue("Tablet"))
        assertEquals(FormFactor.TV, FormFactor.fromValue("TV"))
        assertEquals(FormFactor.WEARABLE, FormFactor.fromValue("Wearable"))
        assertEquals(FormFactor.ANDROID_AUTOMOTIVE, FormFactor.fromValue("Android Automotive"))
        assertEquals(FormFactor.CHROMEBOOK, FormFactor.fromValue("Chromebook"))
        assertEquals(FormFactor.GOOGLE_PLAY_GAMES_ON_PC, FormFactor.fromValue("Google Play Games on PC"))
        assertEquals(FormFactor.UNKNOWN, FormFactor.fromValue("Unknown"))
    }

    @Test
    fun `fromCsvValue throws IllegalArgumentException for invalid CSV value`() {
        // Tests that fromValue throws for unknown values
        val exception =
            assertFailsWith<IllegalArgumentException> {
                FormFactor.fromValue("Invalid Form Factor")
            }
        assertEquals("Unknown form factor: Invalid Form Factor", exception.message)
    }

    @Test
    fun `fromValueOrNull returns correct FormFactor for valid values`() {
        assertEquals(FormFactor.PHONE, FormFactor.fromValueOrNull("Phone"))
        assertEquals(FormFactor.TABLET, FormFactor.fromValueOrNull("Tablet"))
        assertEquals(FormFactor.TV, FormFactor.fromValueOrNull("TV"))
        assertEquals(FormFactor.WEARABLE, FormFactor.fromValueOrNull("Wearable"))
        assertEquals(FormFactor.ANDROID_AUTOMOTIVE, FormFactor.fromValueOrNull("Android Automotive"))
        assertEquals(FormFactor.CHROMEBOOK, FormFactor.fromValueOrNull("Chromebook"))
        assertEquals(FormFactor.GOOGLE_PLAY_GAMES_ON_PC, FormFactor.fromValueOrNull("Google Play Games on PC"))
        assertEquals(FormFactor.UNKNOWN, FormFactor.fromValueOrNull("Unknown"))
    }

    @Test
    fun `fromCsvValueOrNull returns null for invalid CSV value`() {
        // Tests that fromValueOrNull returns null for unknown values
        assertNull(FormFactor.fromValueOrNull("Invalid Form Factor"))
        assertNull(FormFactor.fromValueOrNull(""))
        assertNull(FormFactor.fromValueOrNull("phone")) // case sensitive
    }

    @Test
    fun `getAllValues returns all form factor values`() {
        val expectedValues =
            listOf(
                "Phone",
                "Tablet",
                "TV",
                "Wearable",
                "Android Automotive",
                "Chromebook",
                "Google Play Games on PC",
                "Unknown",
            )
        assertEquals(expectedValues, FormFactor.getAllValues())
    }

    @Test
    fun `enum values have correct value and description`() {
        assertEquals("Phone", FormFactor.PHONE.value)
        assertEquals("Smartphones and mobile phones", FormFactor.PHONE.description)

        assertEquals("Tablet", FormFactor.TABLET.value)
        assertEquals("Tablet devices with larger screens", FormFactor.TABLET.description)

        assertEquals("TV", FormFactor.TV.value)
        assertEquals("Television and Android TV devices", FormFactor.TV.description)

        assertEquals("Wearable", FormFactor.WEARABLE.value)
        assertEquals("Smartwatches and other wearable devices", FormFactor.WEARABLE.description)

        assertEquals("Android Automotive", FormFactor.ANDROID_AUTOMOTIVE.value)
        assertEquals("In-vehicle Android Automotive systems", FormFactor.ANDROID_AUTOMOTIVE.description)

        assertEquals("Chromebook", FormFactor.CHROMEBOOK.value)
        assertEquals("Chromebook devices running Android apps", FormFactor.CHROMEBOOK.description)

        assertEquals("Google Play Games on PC", FormFactor.GOOGLE_PLAY_GAMES_ON_PC.value)
        assertEquals("PC platform for Android games", FormFactor.GOOGLE_PLAY_GAMES_ON_PC.description)

        assertEquals("Unknown", FormFactor.UNKNOWN.value)
        assertEquals("Unknown or unrecognized form factor", FormFactor.UNKNOWN.description)
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
                FormFactor.UNKNOWN,
            )

        val allFormFactors = FormFactor.values().toSet()
        assertEquals(allFormFactors, testedFormFactors, "All FormFactor enum values should be tested")
    }

    @Test
    fun `form factor values are unique`() {
        val values = FormFactor.getAllValues()
        val uniqueValues = values.toSet()
        assertEquals(values.size, uniqueValues.size, "All form factor values should be unique")
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
        assertEquals("UNKNOWN", FormFactor.UNKNOWN.name)
    }
}
