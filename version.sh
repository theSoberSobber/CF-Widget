#!/bin/bash

# Check if version argument is provided
if [ -z "$1" ]; then
    echo "Usage: ./version.sh <version>"
    echo "Example: ./version.sh 1.0.0"
    exit 1
fi

VERSION=$1

# Update build.gradle.kts
sed -i '' "s/versionCode = [0-9]*/versionCode = $(echo $VERSION | cut -d. -f1)/" app/build.gradle.kts
sed -i '' "s/versionName = \"[^\"]*\"/versionName = \"$VERSION\"/" app/build.gradle.kts

# Update metadata.yml
sed -i '' "s/versionName: [0-9.]*/versionName: $VERSION/" app/metadata.yml
sed -i '' "s/versionCode: [0-9]*/versionCode: $(echo $VERSION | cut -d. -f1)/" app/metadata.yml

echo "Version updated to $VERSION"
echo "Don't forget to:"
echo "1. git add ."
echo "2. git commit -m \"Release version $VERSION\""
echo "3. git tag -a v$VERSION -m \"Release version $VERSION\""
echo "4. git push origin v$VERSION" 