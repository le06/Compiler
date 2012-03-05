package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrBlock extends Ir {
	// ordering between vars and stmts enforced at parse-time.
	private ArrayList<IrVarDecl> var_decls;
	private ArrayList<IrStatement> statements;
	
	public ArrayList<IrVarDecl> getVar_decls() {
		return var_decls;
	}

	public ArrayList<IrStatement> getStatements() {
		return statements;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}