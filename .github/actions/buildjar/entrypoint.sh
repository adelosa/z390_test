#!/bin/sh -l
echo "Hello $1"
javaversion=$(java -version)
echo "::set-output name=javaversion::$javaversion"
# Create build directory
mkdir build
# Compile MessageBox first, otherwise compile fails
javac -d ./build ./src/MessageBox.java -g:none -encoding Windows-1252 -verbose
# Compile remaining modules
javac -d ./build --source-path ./src/*.java -g:none -encoding Windows-1252 -cp ./build -verbose
# Create the Jar
jar cvmf ./src/Z390.MAN ./build/z390.jar ./build/*.class