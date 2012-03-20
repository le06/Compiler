package edu.mit.compilers.codegen.ll;

public interface LLExpression extends LLNode {
    public static enum Type {
        VOID, BOOLEAN, INT;
    }
    
    public Type getType();
    
    // note this is only used when evaluating expressions.
    // don't use this to assign to locations!
	public String addressOfResult();
	
	public void setAddress(String addr);
}