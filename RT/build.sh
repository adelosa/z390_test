#!/bin/bash
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/rt"

asmlg_rt1() {
    filename=$1
    shift
    asmlg ${filename} "@${basedir}/RT/RT1" "$@"
}
asm_rt3() {
    filename=$1
    shift
    asm ${filename} noasm bal notiming stats "$@"
}
asmlg_rt5() {
    filename=$1
    shift
    file1="${builddir}/${filename##*/}"
    printf "SYSUT1=${file1}.TF1 SYSUT2=${file1}.TF2 SYSUT3=${file1}.TF3 asmlg ${filename} bal notiming stats\n"
    SYSUT1=${file1}.TF1 SYSUT2=${file1}.TF2 SYSUT3=${file1}.TF3 asmlg ${filename} bal notiming stats
}
asmlg_rt7() {
    filename=$1
    shift
    asml ${filename} notiming bal
    exec ${filename} notiming stats
}

# clear the generated files
clean

# # dynamic linked subroutines used in testlnk1
asmlg_rt1 test/TESTSUB1
asmlg_rt1 test/TESTSUB4 amode24

####################################
# mz390 macro processor
####################################

asmlg_rt1 test/TESTSYS1 "sysparm(12345)"
asmlg_rt1 test/TESTSYS2 bs2000
asmlg_rt1 test/TESTSYN1
asmlg_rt1 test/TESTSYN2 "sysmac(${basedir}/mac+test)"

asmlg_rt1 test/TESTSYN2 "sysmac(${basedir}/mac+test)"
asmlg_rt1 test/TESTPRM1
asmlg_rt1 test/TESTPRM2
asmlg_rt1 test/TESTPRM3
asmlg_rt1 test/TESTMAC1
asmlg_rt1 test/TESTMAC2 tracep
asmlg_rt1 test/TESTMAC3
asmlg_rt1 test/TESTMAC4
asmlg_rt1 test/TESTMAC5
asmlg_rt1 test/TESTMAC6
asmlg_rt1 test/TESTMAC7
asmlg_rt1 test/TESTMAC8 "sysmac(test+${basedir}/mac)"
asmlg_rt1 test/TESTMAC9 "sysmac(test+${basedir}/mac)" "syscpy(test)"
asmlg_rt1 test/TESTPRO1 "@test/TESTPRO1.OPT" "sysmac(test+${basedir}/mac)" "syscpy(test)"
asmlg_rt1 test/TESTOPR1 tracep

# # JAVA error
# asmlg_rt1 test/TESTOPT1 "@TESTOPT1"

asmlg_rt1 test/TESTORG1 trace
asmlg_rt1 test/TESTSET1
asmlg_rt1 test/TESTSET2
asmlg_rt1 test/TESTSET3
asmlg_rt1 test/TESTACT1
asmlg_rt1 test/TESTCPY1 bal
asmlg_rt1 test/TESTCPY2 bal
asmlg_rt1 test/TESTSQL1 "sysmac(test+${basedir}/mac)" "syscpy(test)"

asm_rt3 test/TESTEQU1
set +e
asm_rt3 test/TESTERR1
asm_rt3 test/TESTERR3
asm_rt3 test/TESTERR8
asm_rt3 test/TESTERR9
set -e
asmlg_rt5 test/TESTPCH1

asmlg_rt5 test/TESTPCH2
IN1=test/TESTPCH3.IN1 IN2=test/TESTPCH3.IN2 OT1=${builddir}/TESTPCH3.OT1 OT2=${builddir}/TESTPCH3.OT2 OT3=${builddir}/TESTPCH3.OT3 asmlg_rt5 test/TESTPCH3

asm test/TESTMCR1.ASM stats bal noasm "sysmac(+.)" "syscpy(+.)" notiming

# asm_rt3 test/TESTTXT1

asmlg_rt1 test/TESTPC1 tracep

asmlg_rt1 test/TESTAIN1

asmlg_rt1 test/TESTAIN2 "syscpy(+TEST)"

####################################
# az390 assembler
####################################

asmlg_rt1 test/TESTINS1 assist

# az390 test/TESTASC5 assist notiming stats

asm_rt3 test/TESTDST1
asmlg_rt1 test/TESTLCT1
asmlg_rt1 test/TESTSYM1
asmlg_rt1 test/TESTUSE1
asmlg_rt1 test/TESTUSE2
asmlg_rt1 test/TESTPRT1
asmlg_rt1 test/TESTDC1 "sysmac(TEST+${basedir}/mac)"
# copy rt\test\TESTDC1.MLC rt\test\TESTASC1.MLC
asmlg_rt1 test/TESTASC1 ascii
asmlg_rt1 test/TESTDC2
asmlg_rt1 test/TESTSDT1
asmlg_rt1 test/TESTLOC1
# copy rt\test\TESTSDT1.MLC rt\test\TESTASC2.MLC
asmlg_rt1 test/TESTASC2 ascii
asmlg_rt1 test/TESTASM1 mcall "sysmac(TEST+${basedir}/mac)"
asmlg_rt1 test/TESTASM2
asmlg_rt1 test/TESTENV1 TRACE

set +e
asm test/TESTERR2 bal notiming stats "ERR(0)"
asm test/TESTERRA bal notiming stats "chkmac(2)"
asm test/TESTLAB1 bal notiming stats "ERR(0)"
set -e

####################################
# lz390 linker
####################################

asmlg_rt1 test/TESTRLD1
asmlg_rt1 test/TESTRLD2 NOLOADHIGH TRACE
asmlg_rt1 test/TESTENT1
asmlg_rt1 test/TESTEXT1
asmlg_rt1 test/TESTEXT2
asmlg_rt1 test/TESTWXT1
asmlg_rt1 test/TESTOBJ1 objhex
asmlg_rt1 test/TESTOBJ2 "@TESTOBJ2"
asmlg_rt1 test/TESTPRM4 "@TESTPRM4"

####################################
# ez390 emulator
####################################

# asmlg_rt7 test/TESTERR4
asmlg_rt7 test/TESTERR5
# asmlg_rt7 test/TESTERR6
# asmlg_rt7 test/TESTERR7
asmlg_rt7 test/TESTINS2 trace
asmlg_rt1 test/TESTINS3 trace
# copy rt\test\TESTINS2.MLC rt\test\TESTASC3.MLC
asmlg_rt1 test/TESTASC3 ascii trace
asmlg_rt1 test/TESTFP1  trace
asmlg_rt1 test/TESTFP2  trace
asmlg_rt1 test/TESTDFP1 trace
asmlg_rt7 test/TESTDFP2 trace
DDTODEMO=${basedir}/build/demo/DEMO.390 asmlg_rt1 test/TESTCAL1
asmlg_rt1 test/TESTCAL2
# asmlg_rt1 test/TESTLNK1
asm test/TESTLNK2  notiming stats
asm test/TESTLNK3  notiming stats
MYLIB="${builddir}" link test/TESTLNK2  notiming stats
exec test/TESTLNK3  notiming stats
# asmlg_rt1 test/TESTLOD1
# asmlg_rt5 test/TESTLOD2
asmlg_rt1 test/TESTXCL4
asmlg_rt1 test/TESTXCL3
asmlg_rt1 test/TESTXCL2
asmlg_rt1 test/TESTXCL1
# asmlg_rt5 test/TESTDCB1
# asmlg_rt5 test/TESTDCB2
# asmlg_rt5 test/TESTDCB3
# asmlg_rt5 test/TESTDCB4
# asmlg_rt5 test/TESTDCB5
# asmlg_rt5 test/TESTDCB6
# asmlg_rt5 test/TESTDCB7
# asmlg_rt5 test/TESTDCB8
# asmlg_rt5 test/TESTDCB9
# asmlg_rt1 test/TESTDCBA
# asmlg_rt5 test/TESTDCBB
# asmlg_rt5 test/TESTDCBC ascii
# asmlg_rt5 test/TESTDCBD
# asmlg_rt5 test/TESTDCBE ascii
# asmlg_rt5 test/TESTDCBF
# asmlg_rt5 test/TESTEOF1
# asmlg_rt1 test/TESTTIM1
asmlg_rt1 test/TESTTIM2
asmlg_rt1 test/TESTECB1 trace
asmlg_rt1 test/TESTMEM1 "mem(32)"
asmlg_rt1 test/TESTMEM2 "mem(2)"
asmlg_rt1 test/TESTMEM3 "mem(32)"
asmlg_rt1 test/TESTMEM4 "mem(2)"
# asmlg_rt5 test/TESTTST1 "test(SYSUT1)"
# copy rt\test\TESTTST1.MLC rt\test\TESTASC4.MLC
# copy rt\test\TESTTST1.TF1 rt\test\TESTASC4.TF1
# asmlg_rt5 test/TESTASC4 "test(SYSUT1)" ascii
# asmlg_rt1 test/TESTCMD1
# asmlg_rt1 test/TESTCMD2
asmlg_rt1 test/TESTCIC1
asm test/TESTCIC2 cics notiming  "sysmac(+${basedir}/cics)" "syscpy(+${basedir}/cics)" stats
# asmlg_rt7 test/TESTDMP1
# asmlg_rt7 test/TESTDMP2 dump
# asmlg_rt7 test/TESTDMP3
# asmlg_rt1 test/TESTDMP4
# asmlg_rt7 test/TESTDMP5 dump ascii
# asmlg_rt1 test/TESTXLT1
# asmlg_rt1 test/TESTBLD1 "sys390(+${builddir}/demo)"
asmlg_rt1 test/TESTSVC1 trace noprotect
asmlg_rt1 test/TESTIPL1
asmlg_rt1 test/TESTIPL2 "ipl(TESTIPL1)"
asmlg_rt1 test/TESTZCV1
asmlg_rt1 test/TESTSPI1 trace
asmlg_rt1 test/TESTSTA1 trace
asml test/TESTSTA3 notiming stats
asmlg_rt1 test/TESTSTA2 trace
asmlg_rt1 test/TESTCTD1
asmlg_rt1 test/TESTCFD1
asmlg_rt1 test/TESTCVB1 trace
asmlg_rt1 test/TESTCVBG trace

# rem test zstrmac basic structures using bootstrap version 1
SYSUT1=test/testspe1.zsm SYSUT2=${builddir}/testspe1.mlc asm test/zstrmac1 NOASM STATS NOTIMING
asmlg_rt1 ${builddir}/TESTSPE1 STATS

# rem translate structured version 2 using bootstrap version 1
SYSUT1=test/TESTSPE2.zsm SYSUT2=${builddir}/TESTSPE2.mlc asm test/ZSTRMAC1 NOASM STATS NOTIMING
asmlg_rt1 ${builddir}/TESTSPE2 STATS

# # translate structured version 2 using bootstrap version 1
# # SYSUT1=test/ZSTRMAC2.zsm SYSUT2=${builddir}/ZSTRMAC2.mlc asm test/ZSTRMAC1 NOASM STATS NOTIMING

# verify TESTZSM1.ZSM translation using zstrmac2 matches zstrmac1
SYSUT1=test/TESTSPE1.zsm SYSUT2=${builddir}/TESTSPE1.txt asm test/ZSTRMAC2 NOASM STATS NOTIMING

# regen ZSTRMAC2 using mz390 support to ver zstrmac2.txt = mlc
SYSUT1=test/ZSTRMAC2.zsm SYSUT2=${builddir}/ZSTRMAC2.txt asm test/ZSTRMAC2.zsm NOASM STATS NOTIMING

# test extensions to ACASE added in zstrmac2 C,X,(v1,v2)
SYSUT1=test/TESTSPE3.zsm SYSUT2=${builddir}/TESTSPE3.mlc asm test/ZSTRMAC2.zsm NOASM STATS NOTIMING
asmlg_rt1 ${builddir}/TESTSPE3

# test zstrmac error messages
set +e  # turn off error trap
SYSUT1=test/TESTSPE4.zsm SYSUT2=${builddir}/TESTSPE4.mlc asm test/ZSTRMAC2.zsm NOASM STATS NOTIMING
set -e  # turn on error trap

# test ZSTRMAC SPM's
asmlg_rt1 test/TESTSPM1
