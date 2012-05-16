package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

public class LlEnv implements LlNode {
    private ArrayList<LlNode> subnodes;
    
    public LlEnv() {
        subnodes = new ArrayList<LlNode>();
    }
    
    public void addNode(LlNode node) {
        subnodes.add(node);
    }

    public ArrayList<LlNode> getSubnodes() {
        return subnodes;
    }

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub
        
    }
}
