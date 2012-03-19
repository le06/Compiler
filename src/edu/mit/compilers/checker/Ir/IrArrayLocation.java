package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llArrayAccess;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llLocation;
import edu.mit.compilers.codegen.ll.llNode;

public class IrArrayLocation extends IrLocation {
    public IrArrayLocation(IrIdentifier name, IrExpression expr) {
        id = name;
        index = expr;
    }
    
	private IrIdentifier id;
	private IrExpression index;

	public IrIdentifier getId() {
		return id;
	}
	public IrExpression getIndex() {
		return index;
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupArrayType(id);
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
		index.accept(v);
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
    public llNode getllRep() {
        return (llNode)(new llArrayAccess(id.getId(), (llExpression)index.getllRep()));
    }

}