#!/bin/bash
set -e
source ../build.sh 
setoptions "${basedir}/build/mvs"
options+=" sysmac(${basedir}/mvs/maclib+${basedir}/mac)"

# clear the generated files
clean
asmlg demo/DEMO @${basedir}/RT/RT1
asml  test/TESTMVS1 bal notiming stats objhex
asml  test/testcvtx bal notiming stats objhex
asml  test/testcvt1 bal notiming stats objhex
