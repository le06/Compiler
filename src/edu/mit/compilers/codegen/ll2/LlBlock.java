package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;
import java.util.Iterator;

public class LlBlock implements LlNode {
    private String method;
    private int id;
    
    private ArrayList<LlNode> instructions;
    private ArrayList<LlBlock> parents;
    private ArrayList<LlBlock> children;
    private String calls; // the name of the method it calls at the end of the block, if applicable.
    private String returnsFrom; // the name of the method that returns to this block, if applicable.
    
    public LlBlock(String method, int id) {
        this.method = method;
        this.id = id;
        
        instructions = new ArrayList<LlNode>();
        parents = new ArrayList<LlBlock>();
        children = new ArrayList<LlBlock>();
        calls = null;
        returnsFrom = null;
    }
    
    public String getMethod() {
        return method;
    }
    
    public int getId() {
        return id;
    }
    
    public ArrayList<LlNode> getInstructions() {
        return instructions;
    }

    public void addInstruction(LlNode instruction) {
        instructions.add(instruction);
    }

    public ArrayList<LlBlock> getParents() {
        return parents;
    }

    public void addParent(LlBlock parent) {
        parents.add(parent);
    }

    public ArrayList<LlBlock> getChildren() {
        return children;
    }

    public void addChild(LlBlock child) {
        children.add(child);
    }

    public static void pairUp(LlBlock parent, LlBlock child) {
        parent.addChild(child);
        child.addParent(parent);
    }
    
    public String getCalls() {
        return calls;
    }
    
    public void setCalls(String calls) {
        this.calls = calls;
    }

    public String getReturnsFrom() {
        return returnsFrom;
    }

    public void setReturnsFrom(String returnsFrom) {
        this.returnsFrom = returnsFrom;
    }

    public String toString() {
        String parentIds = "{", childrenIds = "{";

        Iterator<LlBlock> i = parents.iterator();
        while (i.hasNext()) {
            LlBlock next = i.next();
            parentIds = parentIds + next.getId();
            
            if (i.hasNext()) {
                parentIds = parentIds + ", ";
            }
        }
        parentIds = parentIds + "}";
        
        i = children.iterator();
        while (i.hasNext()) {
            LlBlock next = i.next();
            childrenIds = childrenIds + next.getId();
            
            if (i.hasNext()) {
                childrenIds = childrenIds + ", ";
            }
        }
        childrenIds = childrenIds + "}";
        
        return "BLOCK " + id + ": " + parentIds + childrenIds;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }
}
