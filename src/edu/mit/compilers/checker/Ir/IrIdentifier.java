package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.SemanticChecker;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class IrIdentifier extends Ir implements IrExpression {
    private String id;
    private String symbol;
    private int bp_offset;
    
    public IrIdentifier(String name) {
        id = name;
    }

	public String getId() {
		return id;
	}

	public void setSymbol(String symbol) {
	    this.symbol = symbol;
	}
	
	@Override
	public IrType getExprType(SemanticChecker c) {
		return c.lookupVarType(this);
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

	// offset == 0 implies global.
	// offset > 0 implies local.
	// offset == -1 is an error.
	public void setBpOffset(int offset) {
		bp_offset = offset;
	}
	
	public String toString() {
	    return id;
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
    	return new LLVarLocation(bp_offset, id);
    }
}