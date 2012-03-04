package edu.mit.compilers.checker.Ir;

public class IrStringLiteral extends Ir {
    public IrStringLiteral(String text) {
        literal = text;
    }
    
	String literal;
}