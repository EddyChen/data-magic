#!/bin/bash

# Build script for data-magic backend
# Ensures Java 11 is used for compilation

export JAVA_HOME=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home

echo "Using Java 11 from: $JAVA_HOME"
java -version

echo ""
echo "Building data-magic backend..."
mvn clean package "$@"
