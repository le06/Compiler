package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llPop implements llNode {
    private String target;
    
    public llPop (String location) {
        target = location;
    }
    
    public String getTarget() {
        return target;
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("pop\t" + target + "\n");
    }*/
}
