package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class LLEnvironment implements LLNode {
    ArrayList<LLNode> subnodes;
    
    public LLEnvironment() {
        subnodes = new ArrayList<LLNode>();
    }
    
    public void addNode(LLNode node) {
        subnodes.add(node);
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
        
        for (LLNode node : subnodes) {
            node.accept(v);
        }
    }
    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        for (llNode node : subnodes) {
            node.writeASM(outputStream);
        }
    }*/
}
