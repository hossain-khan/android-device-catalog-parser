/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.7/userguide/building_java_projects.html
 */

plugins {
    // For build.gradle.kts (Kotlin DSL)
    // https://kotlinlang.org/docs/releases.html#release-details
    kotlin("jvm")

    // https://docs.gradle.org/current/userguide/publishing_maven.html
    // See the documentation and examples: https://docs.jitpack.io
    `maven-publish`

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // https://github.com/jeremymailen/kotlinter-gradle
    id("org.jmailen.kotlinter")
}

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // CSV Processor
    // https://github.com/doyaaaaaken/kotlin-csv
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.10.0")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Used for exporting converted data to JSON
    testImplementation("com.google.code.gson:gson:2.11.0")
}

// https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.hossain.android"
            artifactId = "catalogparser"
            version = "1.4"

            from(components["java"])
        }
    }
}
