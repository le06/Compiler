package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<LLNode> getAllChildren() {
        ArrayList<LLNode> x = new ArrayList<LLNode>();
        x.add(malloc);
        return x;
    }

    @Override
    public String getNodeDescription() {
        return "Declare " + label.getName();
    }
    
}
