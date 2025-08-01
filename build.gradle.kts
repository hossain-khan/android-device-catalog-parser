plugins {
    // KtLint - An anti-bikeshedding Kotlin linter with built-in formatter
    // https://github.com/jeremymailen/kotlinter-gradle
    id("org.jmailen.kotlinter") version "5.2.0" apply false

    // Dokka - Documentation Engine for Kotlin
    // https://github.com/Kotlin/dokka
    id("org.jetbrains.dokka") version "2.0.0"
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}