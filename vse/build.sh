#!/bin/bash
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/vse"
options+=" @${basedir}/RT/RT1"   # add required parm file

# clear the generated files
clean

# assemble, link, and execute demo wto 'hello world'
asmlg demo/DEMOVSE1 "sysmac(mac+${basedir}/mac)"
asmlg demo/DEMOVSE2 "sysmac(mac+${basedir}/mac)"
SYSUT1=${builddir}/DEMOVSE2.TF1 SYSUT2=${builddir}/DEMOVSE2.TF2 SYSOUT=${builddir}/DEMOVSE2.TF3 asmlg demo/DEMOVSE3 "sysmac(${basedir}/vse/mac+${basedir}/mac)"

# assemble common application macros as test (not executable pgm)
asmlg test/TESTVSE1 "sysmac(mac+${basedir}/mac)" "MEM(32)"
