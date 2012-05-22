package edu.mit.compilers.codegen.ll2;

public class LlGlobalDecl implements LlNode {

    private LlLabel label;
    private long size = 1;
    
    public LlGlobalDecl(LlLabel label) {
        this.label = label;
    }
    
    public LlLabel getLabel() {
        return label;
    }
    
    public long getSize() {
        return size;
    }

    public String toString() {
        return "GLOBALDECL: " + label.getName();
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }
    
}
