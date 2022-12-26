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

    processRecordsToDb(parsedDevices)
}

private fun processRecordsToDb(parsedDevices: List<AndroidDevice>) {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    val database = DeviceDatabase(driver)
    DeviceDatabase.Schema.create(driver)

    val deviceQueries = database.deviceQueries

    parsedDevices.forEach {
        deviceQueries.insert(
            brand = it.brand,
            device = it.device,
            manufacturer = it.manufacturer,
            model_name = it.modelName,
            ram = it.ram,
            processor_name = it.processorName,
        )
    }
    println("Inserted ${deviceQueries.selectAll().executeAsList().size} records to DB")
}