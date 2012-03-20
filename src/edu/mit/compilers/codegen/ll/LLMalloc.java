package edu.mit.compilers.codegen.ll;

public class LLMalloc implements LLNode {

	private long size; // the number of quadwords (8-byte chunks) to allocate.
	
	public LLMalloc(long size) {
		this.size = size;
	}
	
	public long getSize() {
		return size;
	}
	
	@Override
	public void accept(LLNodeVisitor v) {
		v.visit(this);
	}

}
