#!/bin/sh
set -e;
filename=$1
filename="${filename%.*}"           # strip the extension (usually .mlc)
dir=$(pwd)                          # get current directory
for ext in BAL PRN OBJ LST 390 ERR STA TR* ; do          
    rm -f "${filename}.${ext}"      # remove work files
done
options="sysmac(${dir}/mac+.) syscpy(${dir}/mac+.)"
shift 1
# Assemble
java -classpath ${dir}/build -Xrs mz390 ${filename} ${options} "$@"
cat ${filename}.PRN
# link
java -classpath ${dir}/build -Xrs lz390 ${filename} ${options} "$@"
cat ${filename}.LST