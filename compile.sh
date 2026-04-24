#!/bin/bash
# Compiles the entire project into the ./out directory.
set -e
mkdir -p out
find . -name "*.java" | xargs javac -d out
echo "Compilation successful. Run:  java -cp out Main"
