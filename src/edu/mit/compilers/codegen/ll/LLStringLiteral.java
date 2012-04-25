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
    
    public LLLabel getLabel() {
        return label;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		// refers to a constant address in memory.
		return "$." + label.getName();
	}

	@Override
	public Type getType() {
		// irrelevant. used only as a callout arg.
		return null;
	}

    @Override
    public void setAddress(String addr) {
        // irrelevant. address is already implied by label.
    }
    
    @Override
    public String toString() {
        return label.toString() + ": " + literal; 
    }
    
}
