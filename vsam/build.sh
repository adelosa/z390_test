#!/bin/bash
# Run RTVSAM1.BAT
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/vsam"
options+=" @${basedir}/vsam/RTVSAM"

echo ${options}

# compare repro conversions
reprocomp() {
    hlq1=$1
    hlq2=$2
    opts=$3
    printf "START ==== reprocomp ${hlq1} ${hlq2} ${opts} ===="
    INFILE="data/${hlq1}.${hlq2}1${opts}" OUTFILE="${builddir}/TESTCAT.${hlq1}" exec REPRO
    INFILE="${builddir}/TESTCAT.${hlq1}" OUTFILE="${builddir}/${hlq1}.${hlq2}2${opts}" exec REPRO
    if ! cmp -s "data/${hlq1}.${hlq2}1" "${builddir}/${hlq1}.${hlq2}2"; then
        printf 'Not OK - File compare failed\n'; exit 8
    fi
    printf "END   ==== reprocomp ${hlq1} ${hlq2} ${opts} ====\n\n"
}

# clear the generated files
clean

asml demo/ESF1      # RTVSAM1.BAT:10
asml demo/DEMOCAT   # BUILDCAT.BAT:2

QFILE=data/ESF1.TF1 VFILE=${builddir}/DEMOCAT.ESF1 asmlg demo/ESF1SP  # ESF1DEMO.BAT:6
QFILE=${builddir}/ESF1.TF2 VFILE=${builddir}/DEMOCAT.ESF1 asmlg demo/ESF1SG    # ESF1DEMO.BAT:12
if ! cmp -s "data/ESF1.TF1" "${builddir}/ESF1.TF2"; then              # ESF1DEMO.BAT:15
    printf 'Not OK - File compare failed\n'; exit 8
fi

QFILE=data/ESV1.TF1 VFILE=${builddir}/DEMOCAT.ESV1 asmlg demo/ESV1SP  # ESV1DEMO:7
QFILE=${builddir}/ESV1.TF2 VFILE=${builddir}/DEMOCAT.ESV1 asmlg demo/ESV1SG    # ESV1DEMO:13
if ! cmp -s "data/ESV1.TF1" "${builddir}/ESV1.TF2"; then              # ESV1DEMO:16
    printf 'Not OK - File compare failed\n'; exit 8
fi

asml demo/DEMOCAT                                   # RRF1DEMO:2
VFILE=${builddir}/DEMOCAT.RRF1 asmlg demo/RRF1DEMO  # RRF1DEMO:5

asml demo/DEMOCAT                                   # RRV1DEMO:2
VFILE=${builddir}/DEMOCAT.RRV1 asmlg demo/RRV1DEMO  # RRV1DEMO:5

INFILE="data/KSF1NAME.TF1[RECFM=FT]" OUTFILE="${builddir}/DEMOCAT.KSF1NAME" exec REPRO  # KSFRPO1.BAT:4
INFILE="${builddir}/DEMOCAT.KSF1NAME" OUTFILE="${builddir}/KSF1NAME.TF2[RECFM=FT]"   exec REPRO  # KSFRPO1.BAT:9
if ! diff --strip-trailing-cr "data/KSF1NAME.TF1" "${builddir}/KSF1NAME.TF2"; then      # KSFRPO1.BAT:11
    printf 'Not OK - File compare failed\n'; exit 8
fi

VFILE=${builddir}/DEMOCAT.KSF1NAME asmlg demo/KSF1DEMO  # KSF1DEMO.BAT:3

VFILE=${builddir}/DEMOCAT.KSF1NAME asmlg demo/KSF1SKP1  # KSF1SKP1.BAT:3

# Tests
clean
asml test/TESTCAT notiming stats                        # RTVSAM1.BAT:15
QFILE=data/ESF1.TF1 VFILE=${builddir}/TESTCAT.ESF1 asmlg test/ESF1TEST  # ESF1TEST.BAT:8
VFILE=${builddir}/TESTCAT.ESF1 asmlg test/ESF1ERR1      # ESF1ERR1.BAT:4
VFILE=${builddir}/TESTCAT.ESF1 asmlg test/ESF1SKP1      # ESF1SKP1.BAT:4

VFILE=${builddir}/TESTCAT.ESF2 asmlg test/ESF2TEST      # ESF2TEST.BAT:5
VFILE=${builddir}/TESTCAT.ESF3 asmlg test/ESF3TEST      # ESF3TEST.BAT:5
rm -f ${builddir}/ESF3.VES                              # ESF3GEN1.BAT:4
VFILE=${builddir}/TESTCAT.ESF3 asmlg test/ESF3GEN1      # ESF3GEN1.BAT:5
VFILE=${builddir}/TESTCAT.ESV1 QFILE=${builddir}/ESV1.TQ1 asmlg test/ESV1TEST  # ESV1TEST:8
VFILE=${builddir}/TESTCAT.ESV1 asmlg test/ESV1SKP1      # ESV1SKP1.BAT:4
VFILE=${builddir}/TESTCAT.ESV2 asmlg test/ESV2TEST      # ESV2TEST.BAT:6
VFILE=${builddir}/TESTCAT.ESV3 asmlg test/ESV3TEST      # ESV3TEST.BAT:5
VFILE=${builddir}/TESTCAT.ESV4 asmlg test/ESV4TEST      # ESV4TEST.BAT:5
VFILE=${builddir}/TESTCAT.RRF1 asmlg test/RRF1TEST      # RRF1TEST.BAT:5
VFILE=${builddir}/TESTCAT.RRF1 asmlg test/RRF1SKP1      # RRF1SKP1.BAT:4
VFILE=${builddir}/TESTCAT.RRV1 asmlg test/RRV1TEST      # RRV1TEST.BAT:6
VFILE=${builddir}/TESTCAT.RRV1 asmlg test/RRV1SKP1      # RRV1SKP1.BAT:4

reprocomp ESF1 TF                                       # ESF1RPO1.BAT 

# VFILE=data/TESTCAT.KSF1NAME asmlg test/KSF1TEST         # KSF1TEST.BAT
# # EZ390E error  12 program aborting due to abend U0111

INFILE="data/KSF1NAME.TF1[RECFM=FT]" OUTFILE=${builddir}/TESTCAT.KSF2NAME exec REPRO    # KSF2TST1.BAT:4
VFILE=${builddir}/TESTCAT.KSF2NAME asmlg test/KSF2TST1                      # KSF2TST1.BAT:7

INFILE="data/KSF1NAME.TF1[RECFM=FT]" OUTFILE=${builddir}/TESTCAT.KSF2NAME exec REPRO    # KSF2TST2.BAT:4
VFILE=${builddir}/TESTCAT.KSF2NAME asmlg test/KSF2TST2                      # KSF2TST2.BAT:7

VFILE=${builddir}/TESTCAT.KSF2NAME asmlg test/KSF2TST3                      # KSF2TST3.BAT:3

INFILE="data/KSF1NAME.TF1[RECFM=FT]" OUTFILE=${builddir}/TESTCAT.KSF2NAME exec REPRO notracev  # KSF2TST4.BAT:4
VFILE=${builddir}/TESTCAT.KSF2NAME asmlg test/KSF2TST4 notracev             # KSF2TST4.BAT:7

# call vsam\test\ESF4RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESF4 TF
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESF4.TF1

# call vsam\test\ESV1RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
reprocomp ESV1 TF "[RECFM=V]"

# call vsam\test\ESV5RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESV5 TF
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESF5.TF1

# call vsam\test\RRF1RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp RRF1 TF
# # EZ390E error  12 program aborting due to abend S013 - File not found data/RRF1.TF1

# call vsam\test\RRF2RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp RRF2 TF
# # EZ390E error  12 program aborting due to abend S013 - File not found data/RRF2.TF1

# call vsam\test\RRV1RPO1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp RRV1 TF
# # EZ390E error  12 program aborting due to abend S013 - File not found data/RRV1.TF1

# call vsam\test\ESF1RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESF1 TQ
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESF1.TQ1

# call vsam\test\ESF4RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESF4 TQ "[RECFM=FT]"
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESF4.TQ1

# call vsam\test\ESV1RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESV1 TQ "[RECFM=V]"
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESV1.TQ1

# call vsam\test\ESV5RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp ESV5 TQ "[RECFM=VT]"
# # EZ390E error  12 program aborting due to abend S013 - File not found data/ESV5.TQ1

# call vsam\test\RRF2RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp RRF2 TQ "[RECFM=F]"
# # EZ390E error  12 program aborting due to abend S013 - File not found data/RRF2.TQ1

# call vsam\test\RRV1RPQ1 @vsam\rtvsam %1 %2 %3 %4 %5 %6 %7 %8 %9
# reprocomp RRV1 TQ "[RECFM=V]"
# # EZ390E error  12 program aborting due to abend S013 - File not found data/RRV1.TQ1
