#!/bin/bash
# minify-json.sh
# Minifies JSON files in lib/src/test/resources/

set -e

echo "Minifying JSON files..."
echo ""

# Process android-devices-catalog.json
echo "📄 Processing android-devices-catalog.json"
echo "  🔍 Validating source JSON..."
if jq empty lib/src/test/resources/android-devices-catalog.json 2>/dev/null; then
    echo "  ✅ Source JSON is valid"
    echo "  🗜️  Minifying..."
    jq -c . lib/src/test/resources/android-devices-catalog.json > lib/src/test/resources/android-devices-catalog-min.json
    echo "  🔍 Validating minified JSON..."
    if jq empty lib/src/test/resources/android-devices-catalog-min.json 2>/dev/null; then
        echo "  ✅ Minified JSON is valid"
        echo "  ✅ Created android-devices-catalog-min.json"
    else
        echo "  ❌ ERROR: Minified JSON is invalid!"
        exit 1
    fi
else
    echo "  ❌ ERROR: Source JSON is invalid!"
    exit 1
fi

echo ""

# Process android-devices-catalog-unfiltered.json
echo "📄 Processing android-devices-catalog-unfiltered.json"
echo "  🔍 Validating source JSON..."
if jq empty lib/src/test/resources/android-devices-catalog-unfiltered.json 2>/dev/null; then
    echo "  ✅ Source JSON is valid"
    echo "  🗜️  Minifying..."
    jq -c . lib/src/test/resources/android-devices-catalog-unfiltered.json > lib/src/test/resources/android-devices-catalog-unfiltered-min.json
    echo "  🔍 Validating minified JSON..."
    if jq empty lib/src/test/resources/android-devices-catalog-unfiltered-min.json 2>/dev/null; then
        echo "  ✅ Minified JSON is valid"
        echo "  ✅ Created android-devices-catalog-unfiltered-min.json"
    else
        echo "  ❌ ERROR: Minified JSON is invalid!"
        exit 1
    fi
else
    echo "  ❌ ERROR: Source JSON is invalid!"
    exit 1
fi

echo ""
echo "✅ All JSON files minified successfully"
