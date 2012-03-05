package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrFieldDecl extends IrMemberDecl {
	
	IrType type;
	ArrayList<IrGlobalDecl> globals;
	
	public IrType getType() {
		return type;
	}

	public ArrayList<IrGlobalDecl> getGlobals() {
		return globals;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
}