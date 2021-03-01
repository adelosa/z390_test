#!/bin/bash
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/mfacc"
options+=" @${basedir}\RT\RT1"   # add required parm file

# clear the generated files
clean
asmlg P10DSH1
asmlg P10DSH2
asmlg P10MB1
asmlg P11MODEL
asmlg P11FIND1
asmlg P11DSH1
asmlg P11FIND2
asmlg P11DSH2
asmlg P12DSH1
asmlg P12DSH2
asmlg P12DSH3
asmlg P13SC1
asmlg P13DSH1
asmlg P14MW1
asmlg P14DSH1
asmlg P15DSH1
asmlg P16WR1
asmlg P16DSH1
asmlg P17WR1
asmlg P1C1
asmlg P1DSH1
asmlg P1DSH1A
asmlg P1RAFA1
asmlg P2MD1
asmlg P3LKM1
asmlg P3MM1
asmlg P4AN1
asmlg P4APN2
asmlg P4DSH1
asmlg P4RAFA2
asmlg P4RJ1
asmlg P5MM1
asmlg P6BR1
asmlg P6MM1
asmlg P6PJF1
asmlg P6PL1
asmlg P6RW1
asmlg P7DSH1
asmlg P7EH1
asmlg P7RV1
asmlg P8DSH1
asmlg P8DSH2
asmlg P8LM1
asmlg P8MM1
asmlg P9DSH1
asmlg P9MM1
asmlg P9MM2

