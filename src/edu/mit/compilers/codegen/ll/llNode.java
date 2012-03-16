package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public interface llNode {
    //public abstract void writeASM(Writer outputStream) throws IOException;
    
    public void accept(llNodeVisitor v);
}
