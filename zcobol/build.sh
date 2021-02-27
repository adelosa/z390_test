#!/bin/bash
set -e
source ../build.sh 
setoptions "${basedir}/build/zcobol"

# clear the generated files
clean

# compile
asm  z390/ABORT    "syscpy(+Z390)"
asm  z390/ACCEPT   "syscpy(+Z390)"
asm  z390/DISPLAY  "syscpy(+Z390)"
asm  z390/INSPECT  "syscpy(+Z390)"
asm  z390/ZC390NUC "syscpy(+Z390)"
asml z390/ZC390LIB "syscpy(+Z390)" "sysobj(+${basedir}/build/linklib)" RMODE24
