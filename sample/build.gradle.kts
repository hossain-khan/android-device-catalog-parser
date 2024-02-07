plugins {
    kotlin("jvm")
    application
    id("com.squareup.sqldelight")
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
