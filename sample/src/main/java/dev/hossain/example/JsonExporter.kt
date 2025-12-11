package dev.hossain.example

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

/**
 * Handles JSON export and validation of AndroidDevice data.
 */
class JsonExporter {
    /**
     * Custom JSON adapter to maintain form factor names from CSV.
     */
    private class FormFactorJsonAdapter {
        @ToJson
        fun toJson(formFactor: FormFactor): String = formFactor.value

        @FromJson
        fun fromJson(value: String): FormFactor = FormFactor.fromValueOrNull(value) ?: FormFactor.UNKNOWN
    }

    private val moshi =
        Moshi
            .Builder()
            .add(FormFactorJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    /**
     * Writes device list to a JSON file.
     *
     * @param deviceList List of AndroidDevice objects to export
     * @param filePath Path where the JSON file should be written
     */
    fun writeDeviceListToJson(
        deviceList: List<AndroidDevice>,
        filePath: String,
    ) {
        val type = Types.newParameterizedType(List::class.java, AndroidDevice::class.java)
        val adapter = moshi.adapter<List<AndroidDevice>>(type).indent("    ")
        val json = adapter.toJson(deviceList)

        val file = File(filePath)
        file.parentFile?.mkdirs()

        BufferedWriter(FileWriter(file)).use { out ->
            out.write(json)
        }

        println("✓ Exported ${deviceList.size} devices to: $filePath")
    }

    /**
     * Validates a JSON file against a JSON schema.
     *
     * @param jsonPath Path to the JSON file to validate
     * @param schemaPath Path to the JSON schema file
     * @throws JSONException if validation fails
     */
    fun validateJsonWithSchema(
        jsonPath: String,
        schemaPath: String,
    ) {
        val jsonFile = File(jsonPath)
        val schemaFile = File(schemaPath)

        val jsonText = Files.readString(jsonFile.toPath())
        val schemaText = Files.readString(schemaFile.toPath())

        val schemaJson = JSONObject(schemaText)
        val loader: SchemaLoader =
            SchemaLoader
                .builder()
                .schemaJson(schemaJson)
                .draftV7Support()
                .build()
        val schema: Schema = loader.load().build()

        try {
            val jsonArray = JSONArray(jsonText)
            schema.validate(jsonArray)
            println("✓ JSON validation passed: $jsonPath")
        } catch (exception: JSONException) {
            System.err.println("✗ JSON validation failed for '$jsonPath': ${exception.message}")
            throw exception
        }
    }
}
