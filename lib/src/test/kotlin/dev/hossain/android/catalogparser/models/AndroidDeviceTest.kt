package dev.hossain.android.catalogparser.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

/**
 * Tests for [AndroidDevice] data class.
 */
class AndroidDeviceTest {

    private fun createDevice(
        brand: String = "Brand",
        device: String = "Device",
        manufacturer: String = "Manufacturer",
        modelName: String = "Model"
    ): AndroidDevice {
        return AndroidDevice(
            brand = brand,
            device = device,
            manufacturer = manufacturer,
            modelName = modelName,
            ram = "2GB",
            formFactor = FormFactor.PHONE,
            processorName = "Snapdragon",
            gpu = "Adreno",
            screenSizes = listOf("1080x1920"),
            screenDensities = listOf(480),
            abis = listOf("arm64-v8a"),
            sdkVersions = listOf(28),
            openGlEsVersions = listOf("3.2")
        )
    }

    @Test
    fun `equals should return true for same object instance`() {
        val device = createDevice()
        assertEquals(device, device)
    }

    @Test
    fun `equals should return true for objects with same primary key fields`() {
        val device1 = createDevice(brand = "Google", device = "Pixel 4", manufacturer = "Google", modelName = "Pixel 4")
        val device2 = createDevice(brand = "Google", device = "Pixel 4", manufacturer = "Google", modelName = "Pixel 4")
        assertEquals(device1, device2)
    }

    @Test
    fun `equals should return false for null object`() {
        val device = createDevice()
        assertFalse(device.equals(null))
    }

    @Test
    fun `equals should return false for object of different type`() {
        val device = createDevice()
        val other = "a string"
        assertFalse(device.equals(other))
    }

    @Test
    fun `equals should return false if brand is different`() {
        val device1 = createDevice(brand = "Google")
        val device2 = createDevice(brand = "Samsung")
        assertNotEquals(device1, device2)
    }

    @Test
    fun `equals should return false if device is different`() {
        val device1 = createDevice(device = "Pixel 4")
        val device2 = createDevice(device = "Galaxy S10")
        assertNotEquals(device1, device2)
    }

    @Test
    fun `equals should return false if manufacturer is different`() {
        val device1 = createDevice(manufacturer = "Google")
        val device2 = createDevice(manufacturer = "Samsung")
        assertNotEquals(device1, device2)
    }

    @Test
    fun `equals should return false if modelName is different`() {
        val device1 = createDevice(modelName = "Pixel 4")
        val device2 = createDevice(modelName = "Pixel 4 XL")
        assertNotEquals(device1, device2)
    }

    @Test
    fun `equals should return true if non-primary key fields are different`() {
        val device1 = AndroidDevice(
            brand = "Google",
            device = "Pixel 4",
            manufacturer = "Google",
            modelName = "Pixel 4",
            ram = "6GB",
            formFactor = FormFactor.PHONE,
            processorName = "Snapdragon 855",
            gpu = "Adreno 640",
            screenSizes = listOf("1080x2280"),
            screenDensities = listOf(444),
            abis = listOf("arm64-v8a"),
            sdkVersions = listOf(29),
            openGlEsVersions = listOf("3.2")
        )
        val device2 = AndroidDevice(
            brand = "Google",
            device = "Pixel 4",
            manufacturer = "Google",
            modelName = "Pixel 4",
            ram = "8GB",
            formFactor = FormFactor.TABLET,
            processorName = "Snapdragon 865",
            gpu = "Adreno 650",
            screenSizes = listOf("1440x3040"),
            screenDensities = listOf(537),
            abis = listOf("armeabi-v7a"),
            sdkVersions = listOf(30),
            openGlEsVersions = listOf("3.1")
        )
        assertEquals(device1, device2)
    }

    @Test
    fun `hashCode should be same for objects with same primary key fields`() {
        val device1 = createDevice(brand = "Google", device = "Pixel 4", manufacturer = "Google", modelName = "Pixel 4")
        val device2 = createDevice(brand = "Google", device = "Pixel 4", manufacturer = "Google", modelName = "Pixel 4")
        assertEquals(device1.hashCode(), device2.hashCode())
    }

    @Test
    fun `hashCode should be different for objects with different primary key fields`() {
        val device1 = createDevice(brand = "Google")
        val device2 = createDevice(brand = "Samsung")
        assertNotEquals(device1.hashCode(), device2.hashCode())
    }

    @Test
    fun `hashCode should be same for objects with different non-primary key fields`() {
        val device1 = AndroidDevice(
            brand = "Google",
            device = "Pixel 4",
            manufacturer = "Google",
            modelName = "Pixel 4",
            ram = "6GB",
            formFactor = FormFactor.PHONE,
            processorName = "Snapdragon 855",
            gpu = "Adreno 640",
            screenSizes = listOf("1080x2280"),
            screenDensities = listOf(444),
            abis = listOf("arm64-v8a"),
            sdkVersions = listOf(29),
            openGlEsVersions = listOf("3.2")
        )
        val device2 = AndroidDevice(
            brand = "Google",
            device = "Pixel 4",
            manufacturer = "Google",
            modelName = "Pixel 4",
            ram = "8GB",
            formFactor = FormFactor.TABLET,
            processorName = "Snapdragon 865",
            gpu = "Adreno 650",
            screenSizes = listOf("1440x3040"),
            screenDensities = listOf(537),
            abis = listOf("armeabi-v7a"),
            sdkVersions = listOf(30),
            openGlEsVersions = listOf("3.1")
        )
        assertEquals(device1.hashCode(), device2.hashCode())
    }
}
