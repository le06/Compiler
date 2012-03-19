package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llAssign;
import edu.mit.compilers.codegen.ll.llBinOp;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llLocation;
import edu.mit.compilers.codegen.ll.llNode;

public class IrPlusAssignStmt extends IrStatement {
    public IrPlusAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }    
    
    private IrLocation lhs;
    private IrExpression rhs;
    
	public IrLocation getLeft() {
		return lhs;
	}
	public IrExpression getRight() {
		return rhs;
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	@Override
    public String toString(int spaces_before) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < spaces_before; i++) {
            out.append(" ");
        }
        
        out.append("+=\n");
        out.append(lhs.toString(spaces_before+1));
        out.append(rhs.toString(spaces_before+1));
        return out.toString();
    }
    
    public String toString() {
        return lhs.toString() + " = " + rhs.toString();
    }
    
    @Override
    public llNode getllRep() {
        llLocation expr_lhs = (llLocation)lhs.getllRep(); 
        llBinOp new_expr = new llBinOp((llExpression) expr_lhs, (llExpression)rhs.getllRep(), IrBinOperator.PLUS);
        llAssign out = new llAssign(expr_lhs, (llExpression)new_expr);
        return out;
    }
}