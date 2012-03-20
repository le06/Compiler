package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class LLEnvironment implements LLNode {
    private ArrayList<LLNode> subnodes;
    
    public LLEnvironment() {
        subnodes = new ArrayList<LLNode>();
    }
    
    public void addNode(LLNode node) {
        subnodes.add(node);
    }

    public ArrayList<LLNode> getSubnodes() {
    	return subnodes;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        for (llNode node : subnodes) {
            node.writeASM(outputStream);
        }
    }*/
}
