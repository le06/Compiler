package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLLocation;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLVarLocation;

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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLLocation expr_lhs = (LLLocation)lhs.getllRep(null, null); 
        LLBinaryOp new_expr = new LLBinaryOp((LLExpression) expr_lhs, (LLExpression)rhs.getllRep(null, null), IrBinOperator.MINUS);
        LLAssign out = new LLAssign(expr_lhs, (LLExpression)new_expr);
        return out;
    }
}