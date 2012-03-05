package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

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
	
}