package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llVarDec;

public class IrVarDecl extends Ir {

	IrType type;
	ArrayList<IrLocalDecl> locals = new ArrayList<IrLocalDecl>();
	
	public IrVarDecl(IrType var_type) {
	    type = var_type;
	}
	
	public void addLocal(IrIdentifier id) {
	    locals.add(new IrLocalDecl(id));
	}
	
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
	
	public String toString() {
	    StringBuilder out = new StringBuilder();
	    out.append(type.toString().concat(" "));
	    for (IrLocalDecl i : locals) {
	        out.append(i.toString());
	    }
	    return out.toString();
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
    public llNode getllRep() {
        llEnvironment out = new llEnvironment();
        
        for (IrLocalDecl dec : locals) {
            out.addNode(new llVarDec(dec.getId().getId()));
        }
        
        return out;
    }
}