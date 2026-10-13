#!/bin/sh
# Compile and run the Student Management System (macOS / Linux)
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -d out $(find src -name "*.java") && java -cp out com.sms.Main
