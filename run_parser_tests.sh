#!/bin/bash
echo "Executing all JVM tests in latex-parser module..."
if ./gradlew :latex-parser:cleanJvmTest :latex-parser:jvmTest; then
    echo "✅ Tests passed successfully!"
    exit 0
else
    echo "❌ Tests failed!"
    exit 1
fi
