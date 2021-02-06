# Installation

## Prerequisites

### Java SE Runtime Environment 

You will need to have a Java SE runtime version 6 or above.
You do not need the Java SDK but if you have this already installed, this includes the required Java SE runtime. 

!!! Note
    If you want to work on z390, then you will need a JDK installed to compile, build and debug the application. 

    See [http://z390.sourceforge.net/z390_Java_Source_Compile_and_Debug.htm](http://z390.sourceforge.net/z390_Java_Source_Compile_and_Debug.htm) 
    for more on compiling and debugging java code.

## Download

Download the latest version from the [z390 website](http://www.z390.info).

The distribution file name is in the format of `z390_vnnnn.zip` where `nnnn` is the version
number.

## Install for Windows

Unzip the zip distribution to your system. The zip will extract to a folder with the name `z390_vnnnn` where `nnnn` is the version number.

You can add the `z390\bat` directory to system path in order to be able to run
z390 commands from any Windows command terminal. 

## Install for MacOS

Unzip the zip distribution to your system.

The ideal location that will work with the provided perl scripts is your users `lib` directory.

```text
/Users/{youruser}/lib or ~/lib
```

If the folder does not exist, create it. Unzip the distribution in this folder.

The zip will extract to a folder with the name `z390_vnnnn` where `nnnn` is the version number.

Next add the ż390 perl script to your path by adding a shortcutin your user `bin`
directory. 

```text
/Users/{youruser}/bin or ~/bin
```

If the user `bin` folder does not exist, create it. 
If the `bin` folder is not in your path, you will need to modify your shell init script to add your bin folder to your path (.zshrc for zsh)

Create a shortcut to the `z390.pl` script in your `bin` folder

```shell
ln -s ~/lib/z390_vnnnn/perl/z390.pl ~/bin/z390
```

You can now run z390 command in the Terminal.

## Optional components

Three optional z390 downloads are available. Theses are currently hosted on the
<z390.org> site in the _Download links_ section:

1. `RT.ZIP` – regression tests can be downloaded and installed into the z390
install directory to create `rt` directory with all the regression test source
code used to debug and test z390.
After install, run all regression tests using command `rt\RT.BAT`.

2. `MVS.ZIP` – MVS 3.8 Public Domain macros can be downloaded and installed
into the z390 install directory to create mvs subdirectory with all the public
domain MVS 3.8 source macros which are useful examples for learning MVS
macro code and the common macro interfaces. Note that although z390 can
assemble MVS, z/OS, z/VM, and VSE macros, only the z390 macro library
can be used to assemble, link, and then execute code under J2SE on Windows
and Linux systems because these macros issue z390 specific svc calls. To run
regression tests using MVS macros after install, run `rt\RTMVS.BAT`.

3. `WEBDOC_z390.ZIP` – copy of the www.z390.org website documentation can
be downloaded and installed in z390\webdoc. If the file
z390\webdoc\index.html is found, then the help menu "Guide" will open the
local documentation index page otherwise it will open www.z390.org just like
the help "Support" menu item always does. 

## Troubleshooting the installation

There is an installation verification program which can be run by entering `IVP.BAT`
from the GUI or Windows command line after setting current directory to the z390
install directory. 

This program will display the OS, J2SE, and z390 current
versions and will issue warning messages if the required versions for successful z390
execution are not found. 

The z390 root directory plus the optional `rt` and `mvs`
directory installs will have new file named `Z390.IVP` which contains the current z390
version ID and is checked to verify installed version is compatible with the current
z390.jar executable code. If the return code is not zero, please check the installation
and rerun the IVP command again after resolving install problems. 

If the J2SE version is not at least 6.0+, z390 will not run successfully. 

Be sure to uninstall any older versions which may be causing conflict. If problems persist, first suggestion is to try removing all J2SE versions and all z390 versions including deleting the
residual install directory files as some early versions left read only files which
prevent correct install of current version. Then reboot and reinstall latest J2SE and
z390 versions.