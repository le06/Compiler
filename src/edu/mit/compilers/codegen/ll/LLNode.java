package edu.mit.compilers.codegen.ll;

public interface LLNode {
	public void accept(LLNodeVisitor v);
}
