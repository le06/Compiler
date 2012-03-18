package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llNode;

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
	
	public String toString (int s) {
	    StringBuilder out = new StringBuilder();
	    for (int i = 0; i < s; i++) {
	        out.append(" ");
	    }
	    out.append("BLOCK:\n");
	    for (IrVarDecl v : var_decls) {
	        out.append(v.toString(s+1).concat("\n"));
	    }
	    
	    for (IrStatement st : statements) {
	        out.append(st.toString(s+1).concat("\n"));
	    }
	    
	    return out.toString();
	}

    @Override
    public llNode getllRep() {
        llEnvironment out = new llEnvironment();
        for (IrVarDecl d : var_decls) {
            out.addNode(d.getllRep());
        }
        for (IrStatement s : statements) {
            out.addNode(s.getllRep());
        }
        
        return out;
    }
}