![Kotlin CI with Gradle](https://github.com/amardeshbd/android-device-catalog-parser/workflows/Kotlin%20CI%20with%20Gradle/badge.svg) [![](https://jitpack.io/v/hossain-khan/android-device-catalog-parser.svg)](https://jitpack.io/#hossain-khan/android-device-catalog-parser)


# android-device-catalog-parser
Android Device catalog CSV parser that is available from Google Play developer [console](https://play.google.com/console/about/devicecatalog/).

[![](https://user-images.githubusercontent.com/99822/99319347-5e93f800-2837-11eb-9600-779663f580e3.png)](https://play.google.com/console/about/devicecatalog/)
![](https://user-images.githubusercontent.com/99822/263503515-f5910fb5-02c1-4bef-bdc7-1328085b32d9.png)

### Usage
Follow jitpack [guideline](https://jitpack.io/#hossain-khan/android-device-catalog-parser) for latest instructions.

```groovy
// Step 1. Add the JitPack repository to your build file
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}


// Step 2. Add the dependency
dependencies {
    implementation 'com.github.hossain-khan:android-device-catalog-parser:1.10'
}
```

#### Basic Usage

<details><summary>The library provides a simple way to parse Android Device Catalog CSV files:</summary>

```kotlin
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.AndroidDevice

// Parser is now an object (no instantiation needed)
val csvContent = // Your CSV content as String

// Simple parsing - returns only successfully parsed devices
val devices: List&lt;AndroidDevice&gt; = Parser.parseDeviceCatalogData(csvContent)
println("Successfully parsed ${devices.size} devices")
```

</details>

#### Enhanced Usage with Statistics

<details><summary>For more insights into the parsing process, including information about discarded records:</summary>

```kotlin
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.models.ParseResult

val csvContent = // Your CSV content as String

// Enhanced parsing - returns detailed statistics
val result: ParseResult = Parser.parseDeviceCatalogDataWithStats(csvContent)

println("Parsing Summary:")
println("  Total rows processed: ${result.totalRows}")
println("  Successfully parsed: ${result.successfulCount}")
println("  Discarded: ${result.discardedCount}")
println("  Success rate: ${"%.2f".format(result.successRate)}%")

// Access the successfully parsed devices
val devices = result.devices

// Analyze discard reasons
if (result.discardReasons.isNotEmpty()) {
    println("\nDiscard reasons:")
    result.discardReasons.forEach { (reason, count) ->
        println("  $reason: $count")
    }
}
```

#### Example Output
When parsing a CSV with mixed valid and invalid data:

```
Parsing Summary:
  Total rows processed: 1000
  Successfully parsed: 892
  Discarded: 108
  Success rate: 89.20%

Discard reasons:
  Missing required field: Brand: 45
  Missing required field: GPU: 32
  Unknown form factor: Desktop: 18
  Missing required field: RAM (TotalMem): 13
```
</details>

#### Advanced Configuration

<details><summary>Configure parser behavior for missing data and unknown values:</summary>

```kotlin
import dev.hossain.android.catalogparser.Parser
import dev.hossain.android.catalogparser.ParserConfig
import dev.hossain.android.catalogparser.models.FormFactor

val csvContent = // Your CSV content as String

// Configure parser to use defaults instead of discarding rows
val config = ParserConfig.builder()
    .useDefaultsForMissingFields(true)
    .defaultStringValue("Unknown")          // Use "Unknown" for missing string fields
    .defaultIntValue(0)                     // Use 0 for missing integer fields  
    .defaultFormFactor(FormFactor.PHONE)    // Use PHONE for unknown form factors
    .build()

// Parse with configuration
val devices = Parser.parseDeviceCatalogData(csvContent, config)
val result = Parser.parseDeviceCatalogDataWithStats(csvContent, config)

println("With defaults: ${result.successfulCount} devices parsed (${result.successRate}% success)")
```

#### Configuration Options

| Option                        | Default | Description                                                                                 |
|-------------------------------|---------|---------------------------------------------------------------------------------------------|
| `useDefaultsForMissingFields` | `false` | Use default values instead of discarding rows with missing required fields                  |
| `defaultStringValue`          | `""`    | Default value for missing string fields (e.g., "Unknown", "N/A")                            |
| `defaultIntValue`             | `0`     | Default value for missing integer fields                                                    |
| `defaultFormFactor`           | `null`  | Default form factor for unknown values. If `null`, unknown form factors are still discarded |

#### Before vs After Configuration

```kotlin
// Default behavior (backward compatible)
val defaultResult = Parser.parseDeviceCatalogDataWithStats(csvContent)
// Result: 22,751 devices parsed (93.50% success rate)

// With configuration to include all data
val configResult = Parser.parseDeviceCatalogDataWithStats(csvContent, config)  
// Result: 24,332 devices parsed (100% success rate)
```

</details>

### CSV Snapshot
![](https://user-images.githubusercontent.com/99822/99319610-cf3b1480-2837-11eb-8a60-532d974c2151.png)

### Java/Kotlin output
The CSV is parsed into a list of `AndroidDevice` [class](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/main/kotlin/dev/hossain/android/catalogparser/models/AndroidDevice.kt).

Here is a snapshot of parsed [CSV file](https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv)
![](https://github.com/user-attachments/assets/616aaf39-c179-4847-b965-df226b266026)


## Snapshot Files
Device catalog can always be downloaded from the [Google Play Console](https://play.google.com/console/about/devicecatalog/)

### CSV
Here is snapshot taken on August 2025
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.csv

### JSON
Here is the converted JSON of the snapshot above

* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog-min.json
* https://github.com/amardeshbd/android-device-catalog-parser/blob/main/lib/src/test/resources/android-devices-catalog.json
