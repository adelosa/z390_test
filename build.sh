#!/bin/bash
# Standard z390 functions. Source this file to use

basedir="$(dirname "$(pwd)")"       # root z390 source folder
builddir="${basedir}/build"         # build folder

setoptions() {
    builddir=$1         # build folder 
    options="sysmac(+${basedir}/mac+.) syscpy(+${basedir}/mac+.)"
    options+=" SYSERR(${builddir}) SYSOBJ(${builddir}+${basedir}/build/linklib) SYSLST(${builddir})"
    options+=" SYSPCH(${builddir}) SYSPRN(${builddir})"
    options+=" SYS390(${builddir}+${basedir}/build/linklib) SYSBAL(${builddir}) SYSLOG(${builddir})"
}

# clean generated files
clean() {
    rm -rf "${builddir}"
    mkdir -p "${builddir}"
}

# asm only
asm() {
    filename=$1
    printf "\nAssemble ${filename}\n\n"
    shift
    java -classpath ${basedir}/build -Xrs mz390 ${filename} ${options} "$@"
}

# asm and link
asml() {
    filename=$1
    printf "\nAssemble and link ${filename}\n\n"
    shift 
    java -classpath ${basedir}/build -Xrs mz390 ${filename} ${options} "$@"
    java -classpath ${basedir}/build -Xrs lz390 ${filename} ${options} "$@"
}

# asm, link and go
asmlg() {
    filename=$1
    printf "\nAssemble, link and go ${filename}\n\n"
    shift 
    java -classpath ${basedir}/build -Xrs mz390 ${filename} ${options} "$@"
    java -classpath ${basedir}/build -Xrs lz390 ${filename} ${options} "$@"
    set +e
    java -classpath ${basedir}/build -Xrs ez390 ${filename} ${options} "$@"
    set -e
}

# Exec/run
exec() {
    filename=$1
    printf "\nExec ${filename}\n"
    shift 
    set +e
    java -classpath ${basedir}/build -Xrs ez390 ${filename} ${options} "$@"
    set -e
}
