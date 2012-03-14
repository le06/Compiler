package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llUnaryNeg extends llNode {

    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("imul $-1, ");
    }

}
