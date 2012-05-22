package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;
import java.util.Iterator;

public class LlMethodDecl implements LlNode {
    
    public enum MethodType {
        VOID,
        INT,
        BOOLEAN
    }
    
    private MethodType type;
    private String method_name;
    private ArrayList<LlTempLoc> args;
    private int num_args;
    private ArrayList<LlBlock> blocksInOrder;
    private LlBlock returnBlock; // control flow goes into this 'sink' for every return statement in the method.
    
    public LlMethodDecl(MethodType t, String name, int num_args) {
        type = t;
        method_name = name;
        args = new ArrayList<LlTempLoc>();
        this.num_args = num_args;
    }
    
    public MethodType getType() {
        return type;
    }
    
    public String getName() {
        return method_name;
    }
    
    public void addArg(String name) {
        args.add(new LlTempLoc(name));
    }
    
    public ArrayList<LlTempLoc> getArgs() {
        return args;
    }
    
    public int getNumArgs() {
        return num_args;
    }

    public void addBlock(LlBlock b) {
        blocksInOrder.add(b);
    }
    
    public void setBlocksInOrder(ArrayList<LlBlock> blocks) {
        blocksInOrder = blocks;
    }
    
    public ArrayList<LlBlock> getBlocksInOrder() {
        return blocksInOrder;
    }
    
    public void setReturnBlock(LlBlock b) {
        returnBlock = b;
    }
    
    public LlBlock getReturnBlock() {
        return returnBlock;
    }
    
    public String toString() {
        String signature = "(";
        Iterator<LlTempLoc> i = args.iterator();
        while (i.hasNext()) {
            signature = signature + i.next().toString();
            if (i.hasNext()) {
                signature = signature + ", ";
            }
        }
        signature = signature + ")";
        
        return "METHODDECL: " + method_name + signature;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }
    
}
