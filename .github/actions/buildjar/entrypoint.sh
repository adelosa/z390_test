#!/bin/sh -l
set -e
echo "::set-output name=javaversion::$(java -version)"
# Create build directory
mkdir build
# Compile MessageBox first, otherwise compile fails
javac -d ./build ./src/MessageBox.java -g:none -encoding Windows-1252 -verbose
# Compile remaining modules
javac -d ./build --source-path ./src/*.java -g:none -encoding Windows-1252 -cp ./build -verbose
# Create the Jar
jar cvmf ./src/Z390.MAN ./build/z390.jar ./build/*.class

# build and run asm components
cd linklib && ./build.sh && cd ..
cd assist && ./build.sh && cd ..
cd demo && ./build.sh && cd ..
cd mfacc && ./build.sh && cd ..
cd mvs && ./build.sh && cd ..
cd vsam && ./build.sh && cd ..

cd zcobol && ./build.sh && cd ..
