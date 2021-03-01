#!/bin/bash
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/linklib"

cleanold() {
    for ext in 390 ERR LST OBJ PRN LOG PCH STA BAL; do
        rm -f *.${ext}
    done
}

# clear the generated files
clean

# compile
asml REPRO bal notiming stats
asml SUPERZAP bal notiming stats
asml UNREF ascii bal notiming stats
asm FPCONVRT bal notiming stats
asm FPCONMFC bal notiming stats
asm CVTTOHEX bal notiming stats
asm SQXTR bal notiming stats
asm DAT bal notiming stats

asmlg IEFBR14 "sysobj(+${builddir})"
asmlg TESTFPC1 "sysobj(+${builddir})"
asmlg TESTFPC2 "sysobj(+${builddir})"

asm  RTGENDIR noasm bal notiming stats "sysparm(RTGENDIR)"
asm  RTGENCMP noasm bal notiming stats "sysparm(RTGENCMP)"

SYSUT1=zstrmac.zsm SYSUT2=zstrmac.txt asm ZSTRMAC noasm stats

# ZAPDEMO1
cp ZAPDEMO1.DAT ZAPDEMO1.DEMO
temp_file=$(mktemp)
cat >${temp_file} <<EOL
* DEMO FOR SUPERZAP TO CHANGE '111' TO '222' IN 'AAA111BBB' TWICE    
 NAME ZAPDEMO1.DEMO
 DUMP
 ASCII
 DUMP
 FIND 'AAA111BBB'
 VER  *+3 '111'
 REP  *+3 '222'
 DUMP * 9
 LOOP
 HELP
EOL
SYSIN=${temp_file} SYSPRINT=${builddir}/ZAPDEMO1.RPT exec SUPERZAP stats notiming
rm ${temp_file}
echo "*****"
cat ZAPDEMO1.DEMO
echo "*****"
rm ZAPDEMO1.DEMO

# ZAPDEMO2
temp_file=$(mktemp)
zap="* ZAPDEMO2 DUMP SUPERZAP.390 IN EBCDIC\n NAME ${builddir}/SUPERZAP.390\n DUMP"
printf "${zap}" > ${temp_file}
SYSIN=${temp_file} SYSPRINT=${builddir}/ZAPDEMO2.RPT exec SUPERZAP stats notiming
rm ${temp_file}
