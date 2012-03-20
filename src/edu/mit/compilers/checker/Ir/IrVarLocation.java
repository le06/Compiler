package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class IrVarLocation extends IrLocation {
    public IrVarLocation(IrIdentifier var) {
        id = var;
    }
    
	private IrIdentifier id;
	private int bp_offset;

	public IrIdentifier getId() {
		return id;
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupVarType(id);
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

	public void setBpOffset(int offset) {
		bp_offset = offset;
	}
	
	public String toString() {
	    return id.toString();
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return new LLVarLocation(bp_offset, id.getId());
    }
}