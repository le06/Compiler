package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrFieldDecl extends IrMemberDecl {
    IrType type;
    ArrayList<IrGlobalDecl> globals = new ArrayList<IrGlobalDecl>();
    
    public IrFieldDecl(IrType field_type) {
        type = field_type;
    }
    
    public void addGlobal(IrGlobalDecl id) {
        globals.add(id);
    }
	
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
	
	public String toString(int s) {
	    StringBuilder out = new StringBuilder();
	    for (int i = 0; i < s; i++) {
	        out.append(" ");
	    }
	    out.append(type.toString().concat("\n"));
	    
	    for (IrGlobalDecl g : globals) {
	        for (int i = 0; i < s; i++) {
	            out.append(" ");
	        }
	        out.append(g.toString().concat("\n"));
	    }
	     
	    return out.toString();
	}

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return null;
    }
}