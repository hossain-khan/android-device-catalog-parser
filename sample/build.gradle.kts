plugins {
    kotlin("jvm")
    application
    id("app.cash.sqldelight")
    id("org.jmailen.kotlinter")
}

sqldelight {
    // This will be the name of the generated database class.
    databases {
        create("DeviceDatabase") {
            packageName.set("dev.hossain.example")
        }
    }
}

group = "dev.hossain.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation("app.cash.sqldelight:sqlite-driver:2.1.0")
    implementation("com.github.erosb:everit-json-schema:1.14.6") // https://github.com/erosb/everit-json-schema
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

// Configure JVM toolchain
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

kotlinter {
    ktlintVersion = "1.7.1"
    // Due to generated code at sample/build/generated/sqldelight/code
    ignoreFormatFailures = true
    ignoreLintFailures = true
    reporters = arrayOf("checkstyle", "plain")
}