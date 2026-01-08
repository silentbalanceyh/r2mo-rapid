#!/bin/bash

# check input
if [ -z "$1" ]; then
    echo "Usage: ./mvn-version.sh <new-version> [rachel-momo-version]"
    exit 1
fi

NEW_VERSION=$1
MOMO_VERSION=$2

echo "Start to update project version to $NEW_VERSION..."

# 1. Update project version
mvn versions:set -DnewVersion=$NEW_VERSION

# 2. Update rachel-momo-0216 parent version if provided
if [ -n "$MOMO_VERSION" ]; then
    echo "Updating parent version (rachel-momo-0216) to $MOMO_VERSION..."

    # Update root pom.xml
    # Match artifactId rachel-momo-0216 and replace the version in the next line
    sed -i '' "/<artifactId>rachel-momo-0216<\/artifactId>/{n;s|<version>.*</version>|<version>${MOMO_VERSION}</version>|;}" pom.xml

    # Update r2mo-0216 pom.xml
    if [ -f "r2mo-0216/pom.xml" ]; then
        sed -i '' "/<artifactId>rachel-momo-0216<\/artifactId>/{n;s|<version>.*</version>|<version>${MOMO_VERSION}</version>|;}" r2mo-0216/pom.xml
    fi

    echo "Parent version updated."
fi

echo "Version updated to $NEW_VERSION successfully."

