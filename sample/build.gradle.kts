plugins {
    kotlin("jvm")
    application
    id("com.squareup.sqldelight")
    id("org.jmailen.kotlinter")
}

sqldelight {
    // This will be the name of the generated database class.
    database(name = "DeviceDatabase") {
        packageName = "dev.hossain.example"
    }
}

group = "dev.hossain.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
    implementation("com.github.erosb:everit-json-schema:1.14.6") // https://github.com/erosb/everit-json-schema
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
