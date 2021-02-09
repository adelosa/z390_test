#!/bin/sh -l
echo "Hello $1"
javaversion=$(java -version)
echo "::set-output name=javaversion::$javaversion"
# Create build directory
mkdir build
# Compile MessageBox first, otherwise compile fails
javac -d ./build ./src/MessageBox.java -g:none -encoding Windows-1252
# Compile remaining modules
javac -d ./build --source-path ./src/*.java -g:none -encoding Windows-1252 -cp ./build
# Create the Jar
jar cmf ./src/z390.man ./build/z390.jar ./build/*.class