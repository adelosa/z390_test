# Introduction

## What is z390?

The z390 toolkit provides a way to develop, test, and deploy IBM mainframe compatible
HLASM assembler programs using any computer that supports Java Version 6 or above.

## What is HLASM?

HLASM stands for High Level Assembler and it's an IBM software product for 
creating assembler programs targeted at the z/Architecture platform.

BAL or Basic Assembler Language is the base for the HLASM syntax and 
wikipedia has the page [IBM Basic Assembly Language and successors](https://en.wikipedia.org/wiki/IBM_Basic_Assembly_Language_and_successors)
which provides a good description of the language.

```hlasm
         PRINT NOGEN
         START 0
         YREGS
BEGIN    SUBENTRY
         WTO   'Hello world!'
         RETURN
         END   BEGIN
```

### z390 Features

* Supports latest IBM z196 mainframe instruction set
* ASSIST instruction set
* OS/390 and z/OS compatable macros
* CICS application support via emulation
* VSE compatable macros
* DFSORT compatable sort utility
* VSAM file support including REPRO utility

## z390 alternatives

There are a number of tools you can use to develop HLASM programs.
The following is a non exaustive list of alternative offerings.

### HLASM

This is the software that the z390 toolkit ultimately seeks to replace.
HLASM can only runs on IBM Hardware. 
You can buy an IBM developer setup that provides a mainframe
in a PC that you can use to develop applications for IBM systems.
Alternatively you have access to a real IBM mainframe with HLASM available.
For a modern development environment, you could pair this with the 
IBM Developer for Z IDE and the required integration.
For most people, this option is simply not available. 

### PC/370

This was the original PC Mainframe assembler software created by Don Higgins, the
author of z390. The program is a very old DOS based program and will not run on Windows 10.
It also does not support IBM OS macros. You could use a virtual machine running 
FreeDOS if you want to try it. It is now free.

[Download from Bill Qualls website](https://www.billqualls.com/assembler/)

### Hercules/MVS3.8j/TK4-

You can get the full mainframe experience by running a mainframe simulator.
TK4- is a turn key (TK) distribution of a mid 80's z/OS predecessor called
MVS 3.8j which uses a mainframe simulator called Hercules.
All this comes at zero cost.
This setup can provide you an assembler you can use. This predates the modern
HLASM toolkit and is also limited to S/370 instructions and 24 bit addressing.
You will need to use TSO and the mainframe to program which may not be desirable.

<http://wotho.ethz.ch/tk4-/>

### SATK - Stand alone toolkit

If you are interested in bare metal z/Architecture assembly programming then
this is your tool. It was developed to support Linux on Z/Architecture.

<https://github.com/s390guy/SATK>

!!! Question
    I don't think this can be used for OS based applications but I would need
    to spend some more time investigating to know for sure.

### Commercial HLASM tools

_Tachyon Assembler Workbench_ and _Dignus Systems/ASM Assembler and debugger_ are
2 products that provide HLASM compatable tools. They may be the best tools in 
the world, but they are not free. Cost is unknown.
