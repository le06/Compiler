#!/bin/bash

java -jar ~/src/Compiler/dist/Compiler.jar -target parse $1 > doutout
dot -Tpng dotout > dot.png
eog dot.png
