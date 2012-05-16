package edu.mit.compilers.codegen.ll2;

public class LlArrayDecl implements LlNode {

    private LlLabel label;
    private long size;
    
    public LlArrayDecl(LlLabel label, long size) {
        this.label = label;
        this.size = size;
    }
    
    public LlLabel getLabel() {
        return label;
    }
    
    public long getSize() {
        return size;
    }

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub
        
    }
}
