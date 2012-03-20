package edu.mit.compilers.codegen.ll;

public class LLArrayDecl implements LLNode {

    LLLabel id;
    LLMalloc malloc;
    
    public LLArrayDecl(LLLabel id, long size) {
    	this.id = id;
    	malloc = new LLMalloc(size);
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
}
