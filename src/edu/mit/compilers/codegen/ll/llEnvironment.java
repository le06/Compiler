package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class llEnvironment extends llNode {
    ArrayList<llNode> subnodes;
    
    public llEnvironment() {
        subnodes = new ArrayList<llNode>();
    }
    
    public void addNode(llNode node) {
        subnodes.add(node);
    }

    @Override
    public void writeASM(Writer outputStream) throws IOException {
        for (llNode node : subnodes) {
            node.writeASM(outputStream);
        }
    }
}
