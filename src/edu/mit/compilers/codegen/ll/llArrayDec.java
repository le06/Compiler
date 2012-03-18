package edu.mit.compilers.codegen.ll;

public class llArrayDec implements llNode {
    private String id;
    private long size;
    
    public llArrayDec(String name, long dimension) {
        id = name;
        size = dimension;
    }
    
    public String getId() {
        return id;
    }
    
    public long getSize() {
        return size;
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
}
