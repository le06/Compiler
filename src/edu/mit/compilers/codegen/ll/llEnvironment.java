package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class llEnvironment implements llNode {
    ArrayList<llNode> subnodes;
    
    public llEnvironment() {
        subnodes = new ArrayList<llNode>();
    }
    
    public void addNode(llNode node) {
        subnodes.add(node);
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        for (llNode node : subnodes) {
            node.writeASM(outputStream);
        }
    }*/

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        
        for (llNode node : subnodes) {
            node.accept(v);
        }
    }
}
