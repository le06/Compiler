package edu.mit.compilers.codegen;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLNode;

public class BasicBlock {
    private ArrayList<LLNode> instructions;
    private ArrayList<BasicBlock> children;
    private int num;
    
    public BasicBlock() {
        instructions = new ArrayList<LLNode>();
        children = new ArrayList<BasicBlock>();
    }
    
    public void addInstruction(LLNode n) {
        instructions.add(n);
    }
    
    public void setNum(int n) {
    	num = n;
    }
    
    public void addChild(BasicBlock b) {
        children.add(b);
    }
    
    public ArrayList<BasicBlock> getChildren() {
    	return children;
    }
    
    public int getNum() {
    	return num;
    }
    
    public ArrayList<LLNode> getInstructions() {
    	return instructions;
    }
}
