package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrVarDecl extends Ir {

	IrType type;
	ArrayList<IrLocalDecl> locals;
	
	public IrType getType() {
		return type;
	}

	public ArrayList<IrLocalDecl> getLocals() {
		return locals;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
}