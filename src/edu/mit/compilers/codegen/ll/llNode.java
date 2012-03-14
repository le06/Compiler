package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public abstract class llNode {
    public abstract void writeASM(Writer outputStream) throws IOException;
}
