package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public interface LLNode {
	public void accept(LLNodeVisitor v);
	
    //public abstract void writeASM(Writer outputStream) throws IOException;
}
