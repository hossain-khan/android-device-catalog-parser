package dev.hossain.example

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.AndroidDevice
import java.util.Date

fun main() {
    println("Application run on ${Date()}")

    val parser = Parser()
    val csvFileContent =
        object {}.javaClass.getResourceAsStream("/android-devices-catalog.csv")!!.bufferedReader().readText()
    val parsedDevices: List<AndroidDevice> = parser.parseDeviceCatalogData(csvFileContent)

    println("Parsed ${parsedDevices.size} devices from the catalog CSV file.")

    processRecordsToDb()
}

fun processRecordsToDb() {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    val database = DeviceDatabase(driver)
    DeviceDatabase.Schema.create(driver)


    val playerQueries = database.deviceQueries

    println(playerQueries.selectAll().executeAsList())
// Prints [HockeyPlayer(15, "Ryan Getzlaf")]

    playerQueries.insert(player_number = 10, full_name = "Corey Perry")
    println(playerQueries.selectAll().executeAsList())
// Prints [HockeyPlayer(15, "Ryan Getzlaf"), HockeyPlayer(10, "Corey Perry")]

    val player = HockeyPlayer(10, "Ronald McDonald")
    playerQueries.insertFullPlayerObject(player)
    println(playerQueries.selectAll().executeAsList())
}