package edu.mit.compilers.codegen.ll2;

public class LlLabel implements LlNode {

    private String label;
    
    public LlLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return label;
    }
    
    public void setName(String newName) {
        label = newName;
    }
    
    public String getASMLabel() {
        return ".".concat(label).concat(":");
    }

    public String toString() {
        return getASMLabel();
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }
    
}
