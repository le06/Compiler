package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLLabel implements LLNode {
	
	private String label;
	
	public LLLabel(String label) {
		this.label = label;
	}

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

    public String getName() {
    	return label;
    }
    
    public String getASMLabel() {
        return ".".concat(label).concat(":");
    }

    public void setName(String newName) {
        label = newName;
    }
    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        if (!initialized) {
            throw new RuntimeException("Label has no name assigned!");
        }
        
        outputStream.write(name + ":\n");
    }*/
    
    @Override
    public String toString() {
        return label; 
    }
}
