package edu.mit.compilers.codegen.ll;

public class LLGlobalDecl implements LLNode {
    
    LLLabel id;
    LLMalloc malloc;
    
    public LLGlobalDecl(LLLabel label) {
        this.id = label;
        malloc = new LLMalloc(1);
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

}
