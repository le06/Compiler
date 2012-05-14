#!/bin/sh

suffix='.s'
asm=$2$suffix
java -jar dist/Compiler.jar -target codegen -opt all -o $asm $1
gcc -o $2 $asm

