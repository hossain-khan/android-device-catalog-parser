# Catalog Data

This directory contains the Android device catalog data in various formats, automatically generated from the Google Play Console device catalog.

## Files

- **android-devices-catalog.json** - Filtered catalog with only devices that have complete data
- **android-devices-catalog.min.json** - Minified version of the filtered catalog
- **android-devices-catalog-unfiltered.json** - Unfiltered catalog including devices with missing or incomplete data
- **android-devices-catalog-unfiltered.min.json** - Minified version of the unfiltered catalog
- **android-devices-catalog.csv** - Original CSV data from Google Play Console
- **catalog-metadata.json** - Metadata about the catalog including export date, record counts, and access URLs
- **catalog-metadata-schema.json** - JSON Schema for validating the metadata file

## Public Access

These files are published via GitHub Pages at:
- Base URL: `https://hossain-khan.github.io/android-device-catalog-parser/catalog-data/`

## Metadata

The `catalog-metadata.json` file provides information about:
- When the catalog was last exported
- Total number of records in each catalog variant
- Public URLs to access all catalog files
- Schema and repository information

## Updates

These files are automatically updated by the GitHub Actions workflow when the device catalog is refreshed.
