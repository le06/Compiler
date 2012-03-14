package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llPush extends llNode {
    private String target;
    
    public llPush (String data) {
        target = data;
    }

    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("push\t" + target + "\n");
    }
}
