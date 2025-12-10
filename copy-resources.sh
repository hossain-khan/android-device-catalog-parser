#!/bin/bash
# copy-resources.sh
# Copies device catalog files from sample/src/main/resources to lib/src/test/resources

set -e

echo "Copying device catalog files from sample to lib..."
echo ""

# Define source and destination directories
SRC_DIR="sample/src/main/resources"
DEST_DIR="lib/src/test/resources"

# Copy files with explicit source and destination names
copy_file() {
    local src_name=$1
    local dest_name=$2
    local src_file="$SRC_DIR/$src_name"
    local dest_file="$DEST_DIR/$dest_name"
    
    if [ -f "$src_file" ]; then
        echo "📄 Copying $src_name → $dest_name"
        cp "$src_file" "$dest_file"
        echo "  ✅ Copied to $dest_file"
    else
        echo "  ⚠️  WARNING: Source file not found: $src_file"
    fi
}

# Copy each file with proper mapping
copy_file "android-devices-catalog.json" "android-devices-catalog.json"
copy_file "android-devices-catalog-unfiltered.json" "android-devices-catalog-unfiltered.json"
copy_file "android-devices-catalog.min.json" "android-devices-catalog-min.json"
copy_file "android-devices-catalog-unfiltered.min.json" "android-devices-catalog-unfiltered-min.json"
copy_file "android-devices-catalog.csv" "android-devices-catalog.csv"

echo ""
echo "✅ All files copied successfully"
