#!/bin/bash
set -e
source ../build.sh 
setoptions "${basedir}/build/assist"
export XREAD="" XPRNT="" XPNCH="" XGET="" XPUT=""

setassistvars() {   
    XREAD="$1.XRD"
    XPNCH="$1.XPH"
    XGET="$1.XGT"
    XPRNT="${basedir}/build/assist/$1.XPR"
    XPUT="${basedir}/build/assist/$1.XPT"
}

# clear the generated files
clean

# run assist demos
setassistvars DEMOAST1 && asmlg DEMOAST1 assist notiming stats
setassistvars TESTAST1 && asmlg TESTAST1 assist notiming stats
setassistvars SOLP06 && asmlg SOLP06 assist notiming stats
