package edu.mit.compilers.codegen;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLNode;

public class BasicBlock {
    private ArrayList<LLNode> instructions;
    private ArrayList<BasicBlock> children;
    
    public BasicBlock() {
        instructions = new ArrayList<LLNode>();
        children = new ArrayList<BasicBlock>();
    }
    
    public void addInstruction(LLNode n) {
        instructions.add(n);
    }
    
    public void addChild(BasicBlock b) {
        children.add(b);
    }
    
    public ArrayList<LLNode> getInstructions() {
    	return instructions;
    }
}
