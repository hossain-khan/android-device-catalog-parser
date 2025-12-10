#!/bin/bash
# minify-json.sh
# Minifies JSON files in lib/src/test/resources/

set -e

echo "Minifying JSON files..."

jq -c . lib/src/test/resources/android-devices-catalog.json > lib/src/test/resources/android-devices-catalog-min.json
echo "✅ Created android-devices-catalog-min.json"

jq -c . lib/src/test/resources/android-devices-catalog-unfiltered.json > lib/src/test/resources/android-devices-catalog-unfiltered-min.json
echo "✅ Created android-devices-catalog-unfiltered-min.json"

echo ""
echo "✅ All JSON files minified successfully"
