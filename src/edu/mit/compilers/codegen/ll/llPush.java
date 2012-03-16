package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llPush implements llNode {
    private String target;
    
    public llPush (String data) {
        target = data;
    }
    
    public String getTarget() {
        return target;
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("push\t" + target + "\n");
    }*/

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
}
