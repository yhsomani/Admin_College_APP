#!/bin/bash

# This script generates app/google-services.json from environment variables.
# It uses app/google-services.json.template as a base.

TEMPLATE="app/google-services.json.template"
OUTPUT="app/google-services.json"

if [ ! -f "$TEMPLATE" ]; then
    echo "Error: Template file $TEMPLATE not found."
    exit 1
fi

# Use sed to replace placeholders with environment variables.
# Note: In a real CI environment, these variables should be set.
# Defaulting to placeholders if variables are not set.

sed -e "s/YOUR_PROJECT_NUMBER/${FIREBASE_PROJECT_NUMBER:-YOUR_PROJECT_NUMBER}/g" \
    -e "s/YOUR_PROJECT_ID/${FIREBASE_PROJECT_ID:-YOUR_PROJECT_ID}/g" \
    -e "s/YOUR_STORAGE_BUCKET/${FIREBASE_STORAGE_BUCKET:-YOUR_STORAGE_BUCKET}/g" \
    -e "s/YOUR_MOBILESDK_APP_ID_1/${FIREBASE_APP_ID_1:-YOUR_MOBILESDK_APP_ID_1}/g" \
    -e "s/YOUR_MOBILESDK_APP_ID_2/${FIREBASE_APP_ID_2:-YOUR_MOBILESDK_APP_ID_2}/g" \
    -e "s/YOUR_API_KEY/${FIREBASE_API_KEY:-YOUR_API_KEY}/g" \
    "$TEMPLATE" > "$OUTPUT"

echo "Generated $OUTPUT from $TEMPLATE"
