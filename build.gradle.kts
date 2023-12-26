repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent

    repositories {
        // Required to download KtLint
        mavenCentral()
    }

    // Optionally configure plugin
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
    }
}

// https://github.com/JLLeitschuh/ktlint-gradle/issues/458#issuecomment-814797554
configurations.named("ktlint").configure {
    resolutionStrategy {
        dependencySubstitution {
            substitute(module("com.pinterest:ktlint")).with(variant(module("com.pinterest:ktlint:0.41.0")) {
                attributes {
                    attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class, Bundling.EXTERNAL))
                }
            })
        }
    }
}