package edu.mit.compilers.codegen.ll;

public class LLMalloc implements LLNode {

	long size; // the number of quadwords (8-byte chunks) to allocate.
	
	public LLMalloc(long size) {
		this.size = size;
	}
	
	@Override
	public void accept(LLNodeVisitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
