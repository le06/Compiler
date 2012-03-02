#!/bin/bash

java -jar ~/src/Compiler/dist/Compiler.jar -target parse $1 > dotout
dot -Tpng dotout > dot.png
eog dot.png
rm dotout
