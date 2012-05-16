package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.SemanticChecker;
import edu.mit.compilers.codegen.ll.LLArrayLocation;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLLocation;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrArrayLocation extends IrLocation {
    private IrIdentifier id;
    private String symbol;
    private IrExpression index;
    private long array_size = 0;
    
    public IrArrayLocation(IrIdentifier name, IrExpression expr) {
        id = name;
        index = expr;
    }
	
	public IrIdentifier getId() {
		return id;
	}
	public IrExpression getIndex() {
		return index;
	}

    public String getSymbol() {
        return symbol;
    }
	
	public void setSymbol(String symbol) {
	    this.symbol = symbol;
	}
	
	@Override
	public IrType getExprType(SemanticChecker c) {
		return c.lookupArrayType(id);
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	public void setArraySize(long size) {
		array_size = size;
	}
	
	public String toString() {
        return id.toString() + "[" + index.toString() + "]";
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
        return (LLNode)(new LLArrayLocation(id.getId(), array_size, (LLExpression)index.getllRep(null, null)));
    }

}