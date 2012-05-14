#!/bin/bash

java -jar dist/Compiler.jar -target cfg $1 > dotout
dot -Tpng dotout > dot.png
eog dot.png
rm dotout
