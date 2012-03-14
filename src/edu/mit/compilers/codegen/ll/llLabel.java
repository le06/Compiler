package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llLabel extends llNode {
    private String name;
    private boolean initialized = false;
    
    public llLabel() {
        initialized = false;
    }
    
    public llLabel(String label) {
        name = label;
        initialized = true;
    }
    
    public void setName(String label) {
        name = label;
        initialized = true;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public void writeASM(Writer outputStream) throws IOException {
        if (!initialized) {
            throw new RuntimeException("Label has no name assigned!");
        }
        
        outputStream.write(name + ":\n");
    }
}
