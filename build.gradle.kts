plugins {
    // KtLint - An anti-bikeshedding Kotlin linter with built-in formatter
    // https://github.com/jeremymailen/kotlinter-gradle
    id("org.jmailen.kotlinter") version "4.4.1" apply false

    // Dokka - Documentation Engine for Kotlin
    // https://github.com/Kotlin/dokka
    id("org.jetbrains.dokka") version "1.9.20"
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}