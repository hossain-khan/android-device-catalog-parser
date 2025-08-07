# Android Device Catalog Parser

**ALWAYS** reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

Android Device Catalog Parser is a Kotlin library that parses CSV files from Google Play Console device catalog into structured data. The project has two main modules: a core library (`lib`) and a sample application (`sample`) that demonstrates usage with SQLite database integration.

## Working Effectively

### Prerequisites
- **Java**: Requires Java 17+ (project uses Java 21 toolchain, but CI uses Java 17)
- **Gradle**: Uses Gradle 8.10.2 (downloaded automatically via wrapper)

### Bootstrap and Build Process
1. **Clean project**: `./gradlew clean` -- takes ~80 seconds on first run (Gradle daemon startup). NEVER CANCEL. Set timeout to 120+ seconds.

2. **Run tests**: `./gradlew test` -- takes ~50 seconds. NEVER CANCEL. Set timeout to 90+ seconds.
   - Tests both `lib` and `sample` modules
   - Includes unit tests for parsing logic and database schema validation
   - **CRITICAL**: Sample module has generated SQLDelight code that may show lint warnings but tests will pass

3. **Build project**: `./gradlew build` -- takes ~20 seconds (after clean/test). NEVER CANCEL. Set timeout to 60+ seconds.
   - Builds both modules and runs all verification tasks
   - Includes linting, testing, and packaging

4. **Run sample application**: `./gradlew :sample:run` -- takes ~4 seconds.
   - Parses 22,751 Android devices from CSV and prints statistics
   - Demonstrates the core parsing functionality

### Code Formatting and Linting
- **Format code**: `./gradlew formatKotlin` -- takes ~4 seconds.
  - Uses KtLint 1.7.1 for formatting
  - **NOTE**: Sample module ignores format/lint failures due to generated SQLDelight code
- **Check linting**: `./gradlew lintKotlin` -- takes ~3 seconds.
  - Will show lint errors in generated SQLDelight code - this is expected and acceptable

### CI/CD Commands (GitHub Actions)
The CI runs these exact commands in order:
1. `chmod +x gradlew`
2. `./gradlew test`
3. `./gradlew build`

**ALWAYS** run `./gradlew formatKotlin` before committing to ensure code follows project style.

## Project Structure

### Core Library (`lib/`)
- **Main source**: `lib/src/main/kotlin/dev/hossain/android/catalogparser/`
  - `Parser.kt` - Main CSV parsing logic
  - `DataSanitizer.kt` - Data cleaning utilities 
  - `Config.kt` - CSV column configuration
  - `models/AndroidDevice.kt` - Core data model
  - `models/FormFactor.kt` - Device form factor enum
- **Tests**: `lib/src/test/kotlin/dev/hossain/android/catalogparser/`
  - `ParserTest.kt` - Tests CSV parsing logic
  - `DataSanitizerTest.kt` - Tests data sanitization
- **Build**: Uses Kotlin JVM, Maven publishing, and KtLint
- **Dependencies**: kotlin-csv-jvm for CSV processing

### Sample Application (`sample/`)
- **Main source**: `sample/src/main/java/dev/hossain/example/Main.kt`
  - Demonstrates parsing ~22K Android devices from CSV
  - Shows database integration with SQLDelight
  - Contains commented-out database and JSON export functionality
- **Resources**: 
  - `sample/src/main/resources/android-devices-catalog.csv` - Sample CSV data
  - `sample/src/main/resources/android-devices-catalog-schema.json` - JSON schema
- **Database**: Uses SQLDelight for SQLite database operations
  - Schema defined in `sample/src/main/sqldelight/`
  - Generates database interface code automatically
- **Tests**: `sample/src/test/kotlin/dev/hossain/example/DatabaseSchemaTest.kt`

## Validation

### Manual Testing Scenarios
1. **Core parsing validation**: Run `./gradlew :sample:run` and verify it outputs:
   - "Parsed 22751 devices from the catalog CSV file"
   - Statistics for form factors (14,297 phones, 6,015 tablets, etc.)
   - Lists of unique processors, GPUs, screen sizes, ABIs, SDK versions, etc.

2. **Library validation**: Run `./gradlew :lib:test` and verify:
   - All parsing tests pass
   - Data sanitization tests pass
   - No test failures (should show "BUILD SUCCESSFUL")

3. **Build validation**: Run `./gradlew build` and verify:
   - Both modules compile successfully
   - All tests pass
   - JAR files are created in `lib/build/libs/` and `sample/build/libs/`

### Testing Changes
- **ALWAYS** run `./gradlew formatKotlin` before testing changes
- **ALWAYS** run `./gradlew test` to validate changes don't break existing functionality
- **ALWAYS** run `./gradlew :sample:run` to verify the sample application works end-to-end
- **For library changes**: Focus testing on `./gradlew :lib:test`
- **For sample app changes**: Focus testing on `./gradlew :sample:test` and `./gradlew :sample:run`

## Common Tasks

### Working with CSV Data
- Main CSV parsing logic is in `lib/src/main/kotlin/dev/hossain/android/catalogparser/Parser.kt`
- CSV column configuration in `lib/src/main/kotlin/dev/hossain/android/catalogparser/Config.kt`
- Test CSV data format in `lib/src/test/kotlin/dev/hossain/android/catalogparser/ParserTest.kt`
- Full device catalog CSV in `lib/src/test/resources/android-devices-catalog.csv`

### Working with Generated Code
- **SQLDelight generates database code** in `sample/build/generated/sqldelight/`
- **DO NOT** edit generated files directly
- Generated code will show lint errors - this is expected
- Run `./gradlew :sample:generateMainDeviceDatabaseInterface` to regenerate if needed

### Publishing
- Library is published to JitPack as `com.github.hossain-khan:android-device-catalog-parser`
- Maven publication configured in `lib/build.gradle.kts`
- Run `./gradlew publishToMavenLocal` to test publishing locally

## Key Files Reference

### Build Configuration
```
build.gradle.kts                 - Root project configuration (Dokka, KtLint)
settings.gradle.kts              - Multi-project setup, SQLDelight classpath
lib/build.gradle.kts             - Library module (Kotlin JVM, Maven publishing)
sample/build.gradle.kts          - Sample app (Application, SQLDelight)
gradle/wrapper/gradle-wrapper.properties - Gradle 8.10.2 configuration
```

### Source Code
```
lib/src/main/kotlin/dev/hossain/android/catalogparser/
├── Parser.kt                    - Main CSV parsing logic
├── DataSanitizer.kt            - Data cleaning utilities
├── Config.kt                   - CSV column definitions
└── models/
    ├── AndroidDevice.kt        - Core device data model
    └── FormFactor.kt          - Device form factor enum

sample/src/main/java/dev/hossain/example/Main.kt - Demo application
```

### Test Data
```
lib/src/test/resources/android-devices-catalog.csv - Full device catalog
sample/src/main/resources/android-devices-catalog.json - JSON export example
sample/src/main/resources/android-devices-catalog-schema.json - JSON schema
```

## Timing Expectations

- **Clean**: ~80 seconds (first run with Gradle daemon startup)
- **Test**: ~50 seconds 
- **Build**: ~20 seconds (after test)
- **Format**: ~4 seconds
- **Lint**: ~3 seconds  
- **Sample run**: ~4 seconds

**NEVER CANCEL** build, test, or clean operations. Use timeouts of 120+ seconds for clean, 90+ seconds for test, 60+ seconds for build.

## Known Issues

1. **Dokka V1 deprecation warning** - Expected warning about migrating to Dokka V2
2. **SQLDelight lint errors** - Generated code shows many lint violations but this is acceptable
3. **Sample lint errors** - Some wildcard imports and generated code formatting issues are expected
4. **Java version**: CI uses Java 17, but project specifies Java 21 toolchain (both work)

These issues do not affect functionality and should not be "fixed" as they are either expected warnings or acceptable technical debt.