package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLBoolLiteral;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrBoolLiteral extends Ir implements IrExpression {
    public IrBoolLiteral(boolean val) {
        literal = val;
    }
    
    private boolean literal;

	@Override
	public IrType getExprType(IrNodeChecker c) {
		return new IrType(IrType.Type.BOOLEAN);
	}

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// no need to visit; the parser guarantees validity.
	}
	
	public String toString() {
	    return Boolean.toString(literal);
	}
	
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return (LLNode)(new LLBoolLiteral(literal));
    }
}