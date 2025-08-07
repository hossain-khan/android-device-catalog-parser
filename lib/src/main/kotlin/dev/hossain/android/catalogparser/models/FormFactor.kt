package dev.hossain.android.catalogparser.models

/**
 * Enum representing the various form factors of Android devices found in the Google Play Device Catalog.
 *
 * This enum categorizes devices based on their physical form and intended use case.
 * The values are derived from the official Google Play Console Device Catalog.
 *
 * @see <a href="https://play.google.com/console/about/devicecatalog/">Google Play Device Catalog</a>
 */
enum class FormFactor(
    /**
     * The string value as it appears in the CSV data from Google Play Device Catalog.
     */
    val value: String,
    /**
     * A human-readable description of this form factor.
     */
    val description: String,
) {
    /**
     * Traditional smartphones and mobile phones.
     *
     * This is the most common form factor, representing handheld devices
     * primarily designed for communication, apps, and portable computing.
     *
     * Typical characteristics:
     * - Screen sizes: 4" to 7"
     * - Portrait orientation primary
     * - Touch-based interaction
     * - Cellular connectivity
     */
    PHONE("Phone", "Smartphones and mobile phones"),

    /**
     * Tablet devices with larger screens than phones.
     *
     * Tablets are designed for media consumption, productivity, and applications
     * that benefit from larger screen real estate.
     *
     * Typical characteristics:
     * - Screen sizes: 7" to 13"
     * - Both portrait and landscape orientations
     * - Touch-based interaction
     * - May or may not have cellular connectivity
     */
    TABLET("Tablet", "Tablet devices with larger screens"),

    /**
     * Television and set-top box devices running Android TV.
     *
     * These devices are designed for the living room entertainment experience,
     * optimized for viewing from a distance and remote control interaction.
     *
     * Typical characteristics:
     * - Large screen displays (connected TVs)
     * - Landscape orientation
     * - Remote control navigation
     * - Focus on media and entertainment apps
     */
    TV("TV", "Television and Android TV devices"),

    /**
     * Wearable devices such as smartwatches and fitness trackers.
     *
     * Compact devices designed to be worn on the body, typically focusing
     * on health tracking, notifications, and quick interactions.
     *
     * Typical characteristics:
     * - Small circular or square screens
     * - Touch and voice interaction
     * - Health and fitness sensors
     * - Limited app ecosystem optimized for quick glances
     */
    WEARABLE("Wearable", "Smartwatches and other wearable devices"),

    /**
     * In-vehicle infotainment systems running Android Automotive.
     *
     * These are embedded systems in vehicles, designed for navigation,
     * entertainment, and vehicle integration while driving.
     *
     * Typical characteristics:
     * - Dashboard-mounted displays
     * - Voice and touch interaction optimized for driving
     * - Integration with vehicle systems
     * - Focus on navigation, communication, and media
     */
    ANDROID_AUTOMOTIVE("Android Automotive", "In-vehicle Android Automotive systems"),

    /**
     * Chromebook devices running Android apps.
     *
     * Laptop-style devices that can run Android applications alongside
     * Chrome OS, providing a desktop computing experience.
     *
     * Typical characteristics:
     * - Laptop form factor with keyboard and trackpad
     * - Larger screens suitable for productivity
     * - Multi-window support
     * - Hybrid touch and traditional input methods
     */
    CHROMEBOOK("Chromebook", "Chromebook devices running Android apps"),

    /**
     * Google Play Games on PC platform.
     *
     * A platform that allows Android games to run on Windows PCs,
     * providing mobile gaming experiences on desktop computers.
     *
     * Typical characteristics:
     * - Desktop/laptop computers
     * - Keyboard and mouse input
     * - Optimized for gaming performance
     * - Larger screens and enhanced graphics capabilities
     */
    GOOGLE_PLAY_GAMES_ON_PC("Google Play Games on PC", "PC platform for Android games"),

    /**
     * Unknown or unrecognized form factor.
     *
     * Used as a fallback for future or unexpected values in the device catalog.
     */
    UNKNOWN("Unknown", "Unknown or unrecognized form factor"),
    ;

    companion object {
        /**
         * Returns the FormFactor enum value that matches the given CSV value.
         *
         * @param value The string value from the CSV data
         * @return The corresponding FormFactor enum value
         * @throws IllegalArgumentException if the CSV value doesn't match any known form factor
         */
        fun fromValue(value: String): FormFactor =
            entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown form factor: $value")

        /**
         * Returns the FormFactor enum value that matches the given CSV value, or null if not found.
         *
         * @param value The string value from the CSV data
         * @return The corresponding FormFactor enum value, or null if not found
         */
        fun fromValueOrNull(value: String): FormFactor? = entries.find { it.value == value }

        /**
         * Returns all CSV values as a list for validation or reference purposes.
         *
         * @return List of all valid CSV values
         */
        fun getAllValues(): List<String> = entries.map { it.value }
    }
}
