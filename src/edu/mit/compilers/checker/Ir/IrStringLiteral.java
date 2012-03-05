package edu.mit.compilers.checker.Ir;

public class IrStringLiteral extends Ir {
    public IrStringLiteral(String text) {
        literal = text;
    }
    
	String literal;

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// no need to visit; correctness enforced by scanner.
	}
	
	public String toString() {
	    return literal;
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
}