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
    public void accept(LLNodeVisitor v) {
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
