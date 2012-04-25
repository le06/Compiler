package edu.mit.compilers.codegen.ll;

public class LLMov implements LLNode {

	private String src;
	private String dest;
	
	public LLMov(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}
	
	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	@Override
	public void accept(LLNodeVisitor v) {
		v.visit(this);
	}
	
	@Override
    public String toString() {
        return "mov " + src + ", " + dest;
    }

}
