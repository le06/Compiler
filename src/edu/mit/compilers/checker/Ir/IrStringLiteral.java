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
}