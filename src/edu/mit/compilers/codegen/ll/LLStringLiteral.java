package edu.mit.compilers.codegen.ll;

public class LLStringLiteral implements LLExpression {
    LLLabel label;
    String literal;
    
    public LLStringLiteral(String text, LLLabel l) {
        label = l;
        literal = text;
    }
    
    public String getText() {
        return literal;
    }
    
    public String getLabelName() {
        return label.getName();
    }
    
    public String getLabelASM() {
        return label.getASMLabel();
    }
    
    @Override
<<<<<<< HEAD:src/edu/mit/compilers/codegen/ll/llStringLiteral.java
    public void accept(llNodeVisitor v) {
        label.accept(v);
=======
    public void accept(LLNodeVisitor v) {
>>>>>>> 5c223d490eac039adfa26ffb308b8d6aa47fa082:src/edu/mit/compilers/codegen/ll/LLStringLiteral.java
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		// irrelevant. used only in callouts.
		return null;
	}

	@Override
	public Type getType() {
		// irrelevant. used only in callouts.
		return null;
	}
    
}
