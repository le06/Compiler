package edu.mit.compilers.codegen.ll;

public class LLArrayDecl implements LLNode {

    private LLLabel label;
    private LLMalloc malloc;
    
    public LLArrayDecl(LLLabel label, long size) {
    	this.label = label;
    	malloc = new LLMalloc(size);
    }
    
    public LLLabel getLabel() {
    	return label;
    }
    
    public LLMalloc getMalloc() {
    	return malloc;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
}
