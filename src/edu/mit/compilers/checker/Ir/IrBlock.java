package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrBlock extends Ir {
	// ordering between vars and stmts enforced at parse-time.
	private ArrayList<IrVarDecl> var_decls;
	private ArrayList<IrStatement> statements;
	
	public ArrayList<IrVarDecl> getVarDecls() {
		return var_decls;
	}

	public ArrayList<IrStatement> getStatements() {
		return statements;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		for (IrVarDecl d : var_decls) {
			d.accept(v);
		}
		for (IrStatement s : statements) {
			s.accept(v);
		}
	}
}