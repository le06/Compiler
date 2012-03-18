package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llAssign;
import edu.mit.compilers.codegen.ll.llBinOp;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llVarAccess;

public class IrMinusAssignStmt extends IrStatement {
    public IrMinusAssignStmt(IrLocation loc, IrExpression expr) {
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
        
        out.append("-=\n");
        out.append(lhs.toString(spaces_before+1));
        out.append(rhs.toString(spaces_before+1));
        return out.toString();
    }
    
    public String toString() {
        return lhs.toString() + " = " + rhs.toString();
    }
    @Override
    public llNode getllRep() {
        llVarAccess expr_lhs = new llVarAccess(); // TODO get var info
        llBinOp new_expr = new llBinOp(expr_lhs, (llExpression)rhs.getllRep(), IrBinOperator.MINUS);
        llAssign out = new llAssign("a"); // TODO fix assign
        return out;
    }
}