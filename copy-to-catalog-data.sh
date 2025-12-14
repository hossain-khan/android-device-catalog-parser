#!/bin/bash
# copy-to-catalog-data.sh
# Copies device catalog files to catalog-data/ directory and generates metadata

set -e

echo "Setting up catalog-data directory..."
echo ""

# Define directories
SRC_DIR="sample/src/main/resources"
DEST_DIR="catalog-data"

# Create destination directory if it doesn't exist
mkdir -p "$DEST_DIR"

# Copy files
copy_file() {
    local file_name=$1
    local src_file="$SRC_DIR/$file_name"
    local dest_file="$DEST_DIR/$file_name"
    
    if [ -f "$src_file" ]; then
        echo "📄 Copying $file_name"
        cp "$src_file" "$dest_file"
        echo "  ✅ Copied to $dest_file"
    else
        echo "  ⚠️  WARNING: Source file not found: $src_file"
    fi
}

echo "Copying catalog files to $DEST_DIR/..."
copy_file "android-devices-catalog.json"
copy_file "android-devices-catalog-unfiltered.json"
copy_file "android-devices-catalog.min.json"
copy_file "android-devices-catalog-unfiltered.min.json"
copy_file "android-devices-catalog.csv"

echo ""
echo "Generating catalog metadata..."

# Get current date in ISO 8601 format (portable across Unix systems)
EXPORT_DATE=$(TZ=UTC date +"%Y-%m-%dT%H:%M:%SZ")

# Count total records in the main catalog JSON (array length)
TOTAL_RECORDS=0
if [ -f "$DEST_DIR/android-devices-catalog.json" ]; then
    TOTAL_RECORDS=$(jq 'length' "$DEST_DIR/android-devices-catalog.json")
fi

# Count total records in unfiltered catalog
TOTAL_UNFILTERED_RECORDS=0
if [ -f "$DEST_DIR/android-devices-catalog-unfiltered.json" ]; then
    TOTAL_UNFILTERED_RECORDS=$(jq 'length' "$DEST_DIR/android-devices-catalog-unfiltered.json")
fi

# Base URL for GitHub Pages
BASE_URL="https://hossain-khan.github.io/android-device-catalog-parser/catalog-data"

# Generate metadata JSON
cat > "$DEST_DIR/catalog-metadata.json" << EOF
{
  "catalogExportDate": "$EXPORT_DATE",
  "totalRecords": $TOTAL_RECORDS,
  "totalUnfilteredRecords": $TOTAL_UNFILTERED_RECORDS,
  "catalogs": {
    "filtered": {
      "json": "$BASE_URL/android-devices-catalog.json",
      "minified": "$BASE_URL/android-devices-catalog.min.json",
      "description": "Filtered catalog with only devices that have complete data"
    },
    "unfiltered": {
      "json": "$BASE_URL/android-devices-catalog-unfiltered.json",
      "minified": "$BASE_URL/android-devices-catalog-unfiltered.min.json",
      "description": "Unfiltered catalog including devices with missing or incomplete data"
    },
    "csv": {
      "url": "$BASE_URL/android-devices-catalog.csv",
      "description": "Original CSV data from Google Play Console device catalog"
    }
  },
  "schema": {
    "url": "https://raw.githubusercontent.com/hossain-khan/android-device-catalog-parser/main/sample/src/main/resources/android-devices-catalog-schema.json",
    "description": "JSON Schema for device catalog validation"
  },
  "repository": {
    "url": "https://github.com/hossain-khan/android-device-catalog-parser",
    "documentation": "https://github.com/hossain-khan/android-device-catalog-parser#readme"
  }
}
EOF

echo "  ✅ Generated catalog-metadata.json"

echo ""
echo "✅ All files copied and metadata generated successfully"
