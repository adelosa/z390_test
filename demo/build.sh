#!/bin/bash
set -e
source ../buildsrc.sh 
setoptions "${basedir}/build/demo"

# clear the generated files
clean

# run demos
asmlg DEMO notiming stats
asmlg DEMOWTO1 notiming stats
asmlg DEMOWTO2 notiming stats
asmlg DEMOBMK1 notiming stats
asmlg SIEVE notiming stats
asm DEMONUM1 bal noasm notiming stats
asmlg DEMONUM2 notiming stats
asmlg DEMODFP1 notiming stats
asm DEMOM8Q1 bal noasm nolistcall notiming stats
asmlg STDDEVLB notiming stats
