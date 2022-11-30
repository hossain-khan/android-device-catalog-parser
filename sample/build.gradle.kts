plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "dev.hossain.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}