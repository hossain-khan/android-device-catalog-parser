# Sample Application

This sample application demonstrates the usage of the Android Device Catalog Parser library.

## Overview

The sample app showcases:
- Parsing Android device catalog CSV data
- Generating statistics and reports
- Exporting data to JSON format
- Parser configuration options
- Database integration (SQLDelight)

## Architecture

The application follows a clean, modular architecture with separation of concerns:

### Main Components

#### `Main.kt`
The application entry point. Acts as an orchestrator that coordinates between different components. Keeps the main function clean and focused on high-level workflow.

#### `DeviceCatalogParser`
Handles all CSV parsing operations:
- Load CSV content from resources
- Parse device data with or without statistics
- Support custom parser configurations

#### `StatisticsReporter`
Manages output formatting and reporting:
- Print parsing statistics
- Display device summaries
- Show detailed attribute lists (when needed)
- Report parser configuration results

#### `JsonExporter`
Handles JSON export and validation:
- Export device lists to JSON files
- Validate JSON against schema
- Maintain form factor names from CSV

#### `DatabaseOperations`
Manages SQLite database operations:
- Store devices to database
- Retrieve devices from database
- Validate database integrity

## Running the Sample

```bash
./gradlew :sample:run
```

### Expected Output

The application will:
1. Load and parse ~23,000 Android devices from the CSV catalog
2. Display parsing statistics (success rate, discard reasons)
3. Show device summary (form factors, unique attributes)
4. Demonstrate parser configuration options
5. Export data to JSON files
6. Validate JSON against schema

### Output Structure

```
╔═══════════════════════════════════════════════════════╗
║   Android Device Catalog Parser - Sample App         ║
╚═══════════════════════════════════════════════════════╝

=== Parsing Statistics ===
[Statistics about parsing success rate]

=== Device Catalog Summary ===
[Summary of device distribution and attributes]

=== Parser Configuration Examples ===
[Demonstrates different configuration options]

=== Exporting Data ===
[JSON export and validation results]
```

## Testing

Run the tests with:

```bash
./gradlew :sample:test
```

Tests include:
- Database schema validation
- Form factor enum support
- Record insertion and retrieval

## Database Integration

The sample includes SQLDelight integration for SQLite database operations. The `DatabaseOperations` class provides methods to:
- Store parsed devices in a SQLite database
- Retrieve devices back from the database
- Validate data integrity

To enable database operations, uncomment the relevant code in `Main.kt`.

## Dependencies

Key dependencies used:
- **SQLDelight**: Type-safe SQLite database access
- **Moshi**: JSON serialization/deserialization
- **everit-json-schema**: JSON schema validation
- **kotlin-csv-jvm**: CSV parsing (via the main library)

## Code Style

The sample follows the same code style as the main library:
- KtLint for formatting (v1.7.1)
- Kotlin coding conventions
- Clear separation of concerns
- Comprehensive documentation

## Notes

- The sample ignores format/lint failures for generated SQLDelight code (configured in `build.gradle.kts`)
- JSON files are exported to `sample/src/main/resources/`
- Run `./minify-json.sh` from the project root to create minified JSON versions
