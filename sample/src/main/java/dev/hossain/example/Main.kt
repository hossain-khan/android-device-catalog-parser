package dev.hossain.example

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.ParserConfig
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The main function of the application.
 * It reads the Android devices catalog CSV file, parses it into a list of AndroidDevice objects,
 * and then processes these records into a SQLite database.
 */
fun main() {
    // Print the current date and time to the console.
    println("Application run on ${Date()}")

    // Create a new instance of the Parser class.
    val parser = Parser

    // Read the Android devices catalog CSV file from the resources directory.
    val csvFileContent =
        object {}
            .javaClass
            .getResourceAsStream("/android-devices-catalog.csv")!!
            .bufferedReader()
            .readText()

    // Parse the CSV file content into a list of AndroidDevice objects.
    val parsedDevices: List<AndroidDevice> = parser.parseDeviceCatalogData(csvFileContent)

    // Print the number of parsed devices to the console.
    println("Parsed ${parsedDevices.size} devices from the catalog CSV file.")

    // Also demonstrate the enhanced parsing with statistics
    val parseResult = parser.parseDeviceCatalogDataWithStats(csvFileContent)

    println("\n--- Enhanced Parsing Statistics ---")
    println("Total rows processed: ${parseResult.totalRows}")
    println("Successfully parsed: ${parseResult.successfulCount}")
    println("Discarded: ${parseResult.discardedCount}")
    println("Success rate: ${"%.2f".format(parseResult.successRate)}%")

    if (parseResult.discardReasons.isNotEmpty()) {
        println("\nDiscard reasons:")
        parseResult.discardReasons
            .toList()
            .sortedByDescending { it.second }
            .take(10) // Show top 10 reasons
            .forEach { (reason, count) ->
                println("  $reason: $count")
            }
    }
    println("--- End Statistics ---\n")

    // Demonstrate the new configuration feature
    println("\n--- Parser Configuration Demo ---")

    // Example 1: Use defaults for missing fields
    val configWithDefaults =
        ParserConfig
            .builder()
            .useDefaultsForMissingFields(true)
            .defaultStringValue("Unknown")
            .defaultFormFactor(FormFactor.PHONE)
            .build()

    val resultWithDefaults = parser.parseDeviceCatalogDataWithStats(csvFileContent, configWithDefaults)
    println("With defaults for missing fields:")
    println("  Total rows processed: ${resultWithDefaults.totalRows}")
    println("  Successfully parsed: ${resultWithDefaults.successfulCount}")
    println("  Discarded: ${resultWithDefaults.discardedCount}")
    println("  Success rate: ${"%.2f".format(resultWithDefaults.successRate)}%")

    if (resultWithDefaults.discardReasons.isNotEmpty()) {
        println("  Remaining discard reasons:")
        resultWithDefaults.discardReasons
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .forEach { (reason, count) ->
                println("    $reason: $count")
            }
    }

    // Example 2: Custom defaults
    val customConfig =
        ParserConfig
            .builder()
            .useDefaultsForMissingFields(true)
            .defaultStringValue("N/A")
            .defaultIntValue(0)
            .defaultFormFactor(FormFactor.TABLET)
            .build()

    val customResult = parser.parseDeviceCatalogDataWithStats(csvFileContent, customConfig)
    println("\nWith custom defaults (N/A, 0, TABLET):")
    println("  Successfully parsed: ${customResult.successfulCount}")
    println("  Success rate: ${"%.2f".format(customResult.successRate)}%")

    println("--- End Configuration Demo ---\n")

    // Print all unique form factors from the parsed devices, each value in quotes.
    val formFactorCounts = parsedDevices.groupingBy { it.formFactor }.eachCount()
    println("Form factor counts:")
    formFactorCounts.forEach { (formFactor, count) ->
        println("  \"${formFactor.value}\": $count")
    }

    // Print all unique processors
    val uniqueProcessors = parsedDevices.map { it.processorName }.toSet().sorted()
    println("Unique processors:")
    uniqueProcessors.forEach { println("  \"$it\"") }

    // Print all unique GPUs
    val uniqueGpus = parsedDevices.map { it.gpu }.toSet().sorted()
    println("Unique GPUs:")
    uniqueGpus.forEach { println("  \"$it\"") }

    // Print all unique screen sizes, sorted by width then height
    val uniqueScreenSizes = parsedDevices.flatMap { it.screenSizes }.toSet()
    val sortedScreenSizes =
        uniqueScreenSizes.sortedWith(
            compareBy(
                { it.substringBefore('x').toIntOrNull() ?: 0 },
                { it.substringAfter('x').toIntOrNull() ?: 0 },
            ),
        )
    println("Unique screen sizes:")
    sortedScreenSizes.forEach { println("  \"$it\"") }

    // Print all unique ABIs
    val uniqueAbis = parsedDevices.flatMap { it.abis }.toSet().sorted()
    println("Unique ABIs:")
    uniqueAbis.forEach { println("  \"$it\"") }

    // Print all unique SDK versions
    val uniqueSdkVersions = parsedDevices.flatMap { it.sdkVersions }.toSet().sorted()
    println("Unique SDK versions:")
    uniqueSdkVersions.forEach { println("  \"$it\"") }

    // Print all unique OpenGL ES versions
    val uniqueOpenGlEsVersions = parsedDevices.flatMap { it.openGlEsVersions }.toSet()
    println("Unique OpenGL ES versions:")
    uniqueOpenGlEsVersions.forEach { println("  \"$it\"") }

    // Print all unique screen densities, sorted numerically
    val uniqueScreenDensities = parsedDevices.flatMap { it.screenDensities }.toSet().sorted()
    println("Unique screen densities:")
    uniqueScreenDensities.forEach { println("  \"$it\"") }

    // Write the parsed AndroidDevice objects to a JSON file.
    // writeDeviceListToJson(parsedDevices, "sample/src/main/resources/android-devices-catalog.json")

    // Process the parsed devices into a SQLite database.
    // processRecordsToDb(parsedDevices)
}

/**
 * Processes the parsed Android devices into a SQLite database.
 *
 * @param parsedDevices The list of AndroidDevice objects parsed from the CSV file.
 */
private fun processRecordsToDb(parsedDevices: List<AndroidDevice>) {
    // Create a new File object for the SQLite database.
    val dbFile = File("devices.db")

    // If the database file already exists, delete it.
    if (dbFile.exists()) {
        dbFile.delete()
    }

    // Create a new SQLite driver for the database.
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:devices.db")

    // Create a new DeviceDatabase object with the SQLite driver.
    val database = DeviceDatabase(driver)

    // Create the schema for the DeviceDatabase.
    DeviceDatabase.Schema.create(driver)

    // Get the DeviceQueries object from the DeviceDatabase.
    val deviceQueries = database.deviceQueries

    parsedDevices.forEachIndexed { index, androidDevice ->
        deviceQueries.transaction {
            // Insert the AndroidDevice into the database.
            deviceQueries.insert(
                brand = androidDevice.brand,
                device = androidDevice.device,
                manufacturer = androidDevice.manufacturer,
                model_name = androidDevice.modelName,
                ram = androidDevice.ram,
                form_factor = androidDevice.formFactor.value,
                processor_name = androidDevice.processorName,
                gpu = androidDevice.gpu,
            )
            // Get the ID of the last inserted row inside the transaction
            val deviceId: Long = deviceQueries.lastInsertRowId().executeAsOne()
            println("Inserted new record with id: $deviceId")

            androidDevice.abis.forEach {
                deviceQueries.insertAbi(deviceId, it)
            }
            androidDevice.openGlEsVersions.forEach {
                deviceQueries.insertOpenGlVersion(deviceId, it)
            }
            androidDevice.screenDensities.forEach {
                deviceQueries.insertScreenDensity(deviceId, it.toLong())
            }
            androidDevice.screenSizes.forEach {
                deviceQueries.insertScreenSize(deviceId, it)
            }
            androidDevice.sdkVersions.forEach {
                deviceQueries.insertSdkVersion(deviceId, it.toLong())
            }
        }
    }

    // Get all the Device records from the database.
    val deviceListDb: List<Device> = deviceQueries.selectAll().executeAsList()

    // Print the number of records inserted into the database to the console.
    println("Inserted ${deviceListDb.size} records to DB")

    // Map the Device records from the database to AndroidDevice objects.
    val androidDevicesFromDb: List<AndroidDevice> =
        deviceListDb.map { dbDevice ->
            AndroidDevice(
                brand = dbDevice.brand,
                device = dbDevice.device,
                manufacturer = dbDevice.manufacturer,
                modelName = dbDevice.model_name,
                ram = dbDevice.ram,
                formFactor = FormFactor.fromValue(dbDevice.form_factor),
                processorName = dbDevice.processor_name,
                gpu = dbDevice.gpu,
                screenSizes = deviceQueries.getScreenSize(dbDevice._id).executeAsList().map { it.screen_size },
                screenDensities =
                    deviceQueries
                        .getScreenDensity(dbDevice._id)
                        .executeAsList()
                        .map { it.screen_density.toInt() },
                abis = deviceQueries.getAbi(dbDevice._id).executeAsList().map { it.abi },
                sdkVersions = deviceQueries.getSdkVersion(dbDevice._id).executeAsList().map { it.sdk_version.toInt() },
                openGlEsVersions =
                    deviceQueries
                        .getOpenGlVersion(dbDevice._id)
                        .executeAsList()
                        .map { it.opengl_version },
            )
        }

    // Print the total number of AndroidDevice objects retrieved from the database to the console.
    println("Got all the Android device model back. Total: ${androidDevicesFromDb.size}")

    // Print whether the AndroidDevice objects retrieved from the database are the same as the parsed AndroidDevice objects.
    println("Are they same as source? ${parsedDevices == androidDevicesFromDb}")
}

/**
 * Writes the list of AndroidDevice objects to a JSON file.
 *
 * @param deviceList The list of AndroidDevice objects to write to the JSON file.
 * @param filePath The path of the JSON file to write the AndroidDevice objects to.
 */
fun writeDeviceListToJson(
    deviceList: List<AndroidDevice>,
    filePath: String,
) {
    val moshi =
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    val type = Types.newParameterizedType(List::class.java, AndroidDevice::class.java)
    val adapter = moshi.adapter<List<AndroidDevice>>(type).indent("    ")
    val json = adapter.toJson(deviceList)

    BufferedWriter(FileWriter(filePath)).use { out ->
        out.write(json)
    }

    validateJsonWithSchema(filePath, "sample/src/main/resources/android-devices-catalog-schema.json")
}

fun validateJsonWithSchema(
    jsonPath: String,
    schemaPath: String,
) {
    val jsonText = Files.readString(Paths.get(jsonPath))
    val schemaText = Files.readString(Paths.get(schemaPath))

    // Load schema as JSONObject
    val schemaJson = JSONObject(schemaText)

    val loader: SchemaLoader =
        SchemaLoader
            .builder()
            .schemaJson(schemaJson)
            .draftV7Support() // Using draft v7 as specified in the schema
            .build()
    val schema: Schema = loader.load().build()

    try {
        val jsonArray = JSONArray(jsonText)
        schema.validate(jsonArray) // Throws ValidationException if invalid
        println("JSON validation passed successfully!")
    } catch (exception: JSONException) {
        System.err.println("Failed to parse JSON file '$jsonPath': ${exception.message}")
        throw exception
    }
}
