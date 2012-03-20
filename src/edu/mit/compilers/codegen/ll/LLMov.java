package edu.mit.compilers.codegen.ll;

public class LLMov implements LLNode {

	String src;
	String dest;
	
	public LLMov(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}
	
	@Override
	public void accept(LLNodeVisitor v) {
		v.visit(this);
	}

}
