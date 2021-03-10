#!/bin/bash
# Standard z390 functions. Source this file to use
set -e
basedir="$(dirname "$(pwd)")"       # root z390 source folder
builddir="${basedir}/build"         # build folder
z390jar="${basedir}/build/z390.jar"

setoptions() {
    builddir=$1         # build folder 
    options="sysmac(+${basedir}/mac+.) syscpy(+${basedir}/mac+.)"
    options+=" SYSERR(${builddir}) SYSOBJ(${builddir}+${basedir}/build/linklib) SYSLST(${builddir})"
    options+=" SYSPCH(${builddir}) SYSPRN(${builddir}) SYSTRC(${builddir})"
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
    java -classpath ${z390jar} -Xrs mz390 ${filename} ${options} "$@"
}

# asm and link
asml() {
    filename=$1
    printf "\nAssemble and link ${filename}\n\n"
    shift 
    java -classpath ${z390jar} -Xrs mz390 ${filename} ${options} "$@"
    java -classpath ${z390jar} -Xrs lz390 ${filename} ${options} "$@"
}

# asm, link and go
asmlg() {
    filename=$1
    printf "\nAssemble, link and go ${filename}\n\n"
    shift
    set +e
    java -classpath ${z390jar} -Xrs mz390 ${filename} ${options} "$@"
    exit_status=$?
    set -e
    if [ ${exit_status} -gt 4 ]; then
        return ${exit_status}
    fi
    java -classpath ${z390jar} -Xrs lz390 ${filename} ${options} "$@"
    java -classpath ${z390jar} -Xrs ez390 ${filename} ${options} "$@"
}

# Exec/run
exec() {
    filename=$1
    printf "\nExec ${filename}\n"
    shift 
    set +e
    java -classpath ${z390jar} -Xrs ez390 ${filename} ${options} "$@"
    exit_status=$?
    set -e
    if [ ${exit_status} -gt 4 ]; then
        return ${exit_status}
    fi
}

link() {
    filename=$1
    printf "\nLink ${filename}\n\n"
    shift
    java -classpath ${z390jar} -Xrs lz390 ${filename} ${options} "$@"
}

az390() {
    filename=$1
    printf "\naz390 ${filename}\n\n"
    shift
    java -classpath ${z390jar} -Xrs az390 ${filename} ${options} "$@"
}
