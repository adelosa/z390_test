#!/bin/sh -l
set -e
echo "::set-output name=javaversion::$(java -version)"
# Create build directory
rm -rf build/src
rm -f build/z390.jar
mkdir -p build/src

# Compile MessageBox first, otherwise compile fails
javac -d ./build/src ./src/MessageBox.java -g:none -encoding Windows-1252 -verbose
# Compile remaining modules
javac -d ./build/src --source-path ./src/*.java -g:none -encoding Windows-1252 -cp ./build/src -verbose
# Create the Jar
jar cvmf src/Z390.MAN build/z390.jar -C build/src .

# build and run asm components
cd linklib && ./build.sh && cd ..
cd assist && ./build.sh && cd ..
cd demo && ./build.sh && cd ..
cd mfacc && ./build.sh && cd ..
cd mvs && ./build.sh && cd ..
cd vsam && ./build.sh && cd ..

cd zcobol && ./build.sh && cd ..
