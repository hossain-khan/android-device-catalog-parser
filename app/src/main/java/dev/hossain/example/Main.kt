package dev.hossain.example

import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.AndroidDevice
import java.util.Date

fun main() {
    println("Application run on ${Date()}")

    val parser = Parser()
    val csvFileContent = object {}.javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()
    val parsedDevices: List<AndroidDevice> = parser.parseDeviceCatalogData(csvFileContent)

    println("Parsed ${parsedDevices.size} devices from the catalog CSV file.")
}