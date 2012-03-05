package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrBlock extends Ir {
	// ordering between vars and stmts enforced at parse-time.
	private ArrayList<IrVarDecl> var_decls = new ArrayList<IrVarDecl>();
	private ArrayList<IrStatement> statements = new ArrayList<IrStatement>();
	
	public void addDecl(IrVarDecl dec) {
	    var_decls.add(dec);
	}
	
	public void addStatement(IrStatement line) {
	    statements.add(line);
	}
	
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