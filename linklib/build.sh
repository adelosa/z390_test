#!/bin/bash
# must be run from linklib directory.
# assumes that the z390.jar has been built and is located in build folder 

# Issues
# ======
# RTGENCMP -- link error: LZ390E abort 19 no csect object code text defined for load module linklib/RTGENCMP.OBJ
#             asm - generates file .PCH with the following:
#                     REM TOT COMPARES = 0  TOT ERRORS =0
#                     COPY RTGENCMP.BAT %3
# RTGENDIF -- asm error: RTGENDIF DIF DIR NOT FOUND IN SYSPARM
# RTGENDIR -- link error: LZ390E abort 19 no csect object code text defined for load module linklib/RTGENDIR.OBJ
# ZSTRMAC  -- TRANSLATE Z390 ZSTRMAC EXTENSIONS TO STD HLASM. Not sure what to do with this

set -e
basedir="$(dirname "$(pwd)")"
options="sysmac(+${basedir}/mac+.) syscpy(+${basedir}/mac+.)"

# clean generated files
clean() {
    for ext in 390 ERR LST OBJ PRN LOG PCH; do
        rm -f *.${ext}
    done
}

# asm only
asm() {
    filename=$1
    printf "\nAssemble ${filename}\n\n"
    shift
    java -classpath ../build -Xrs mz390 ${filename} ${options} "$@"
    printf "Assembly output: ${filename}.PRN\n"
}

# asm and link
asml() {
    filename=$1
    printf "\nAssemble and link ${filename}\n\n"
    shift 
    java -classpath ../build -Xrs mz390 ${filename} ${options} "$@"
    printf "Assembly output: ${filename}.PRN\n"
    java -classpath ../build -Xrs lz390 ${filename} ${options} "$@"
    printf "Link output: ${filename}.LST\n"
}

# Exec/run
exec() {
    filename=$1
    printf "\nExec ${filename}\n"
    shift 
    set +e
    java -classpath ../build -Xrs ez390 ${filename} ${options} "$@"
    set -e
}

# clear the generated files
clean

# compile
asml FPCONVRT
asml CVTTOHEX
asml DAT
asml FPCONMFC
asml IEFBR14
asml REPRO
asml SQXTR
asml SUPERZAP
asml UNREF ascii
asm  RTGENCMP
asm  RTGENDIR
# asm  RTGENDIF
asml TESTFPC1
asml TESTFPC2

# run
exec TESTFPC1
exec TESTFPC2

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
SYSIN=${temp_file} SYSPRINT=ZAPDEMO1.RPT exec SUPERZAP
rm ${temp_file}
echo "*****"
cat ZAPDEMO1.DEMO
echo "*****"
rm ZAPDEMO1.DEMO

# ZAPDEMO2
temp_file=$(mktemp)
cat >${temp_file} <<EOL
* ZAPDEMO2 DUMP SUPERZAP.390 IN EBCDIC
 NAME SUPERZAP.390
 DUMP
EOL
SYSIN=${temp_file} SYSPRINT=ZAPDEMO2.RPT exec SUPERZAP
rm ${temp_file}
