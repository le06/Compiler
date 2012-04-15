package edu.mit.compilers.codegen.ll;

public interface LLNode {
	public void accept(LLNodeVisitor v);
	
	// Returns empty list if there are no children
	public java.util.List<LLNode> getAllChildren();
	
	public String getNodeDescription();
}
