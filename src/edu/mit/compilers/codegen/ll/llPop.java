package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llPop extends llNode {
    private String target;
    
    public llPop (String location) {
        target = location;
    }

    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("pop\t" + target + "\n");
    }
}
