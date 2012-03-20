package edu.mit.compilers.codegen.ll;

public class LLGlobalDecl implements LLNode {
    
    private LLLabel label;
    private LLMalloc malloc;
    
    public LLGlobalDecl(LLLabel label) {
        this.label = label;
        malloc = new LLMalloc(1);
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
