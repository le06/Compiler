package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrFieldDecl extends IrMemberDecl {
	
	IrType type;
	ArrayList<IrGlobalDecl> globals;
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
	
}