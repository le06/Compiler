package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.Ir.IrNodeChecker.Type;
import edu.mit.compilers.codegen.ll.llBinOp;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public class IrBinopExpr extends Ir implements IrExpression {
    public IrBinopExpr(IrBinOperator op, IrExpression left, IrExpression right) {
        operator = op;
        lhs = left;
        rhs = right;
    }
    
    private IrBinOperator operator;
    private IrExpression lhs;
    private IrExpression rhs;
    
    public IrBinOperator getOperator() {
		return operator;
	}

	public IrExpression getLeft() {
		return lhs;
	}

	public IrExpression getRight() {
		return rhs;
	}

	public String toString() {
        return lhs.toString() + " " + operator.toString() + " " + rhs.toString();
    }

	@Override
	public IrType getExprType(IrNodeChecker c) {
		IrType lhs_type = lhs.getExprType(c);
		IrType rhs_type = rhs.getExprType(c);

		if (lhs_type.myType == rhs_type.myType) {
			
			switch (operator) {
			// arith_op
			case PLUS:
			case MINUS:
			case MUL:
			case DIV:
			case MOD:
				return new IrType(IrType.Type.INT);
			// rel_op
			case LT:
			case GT:
			case LEQ:
			case GEQ:
				return new IrType(IrType.Type.BOOLEAN);
			case EQ:
			case NEQ:
				return new IrType(IrType.Type.BOOLEAN);
			case AND:
			case OR:
				return new IrType(IrType.Type.BOOLEAN);
			}
			
			
			return new IrType(lhs_type.myType);
		} else {
			return new IrType(IrType.Type.MIXED);
		}
		
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	   public String toString (int s) {
	        StringBuilder out = new StringBuilder();
	        for (int i = 0; i < s; i++) {
	            out.append(" ");
	        }
	        out.append(operator.toString().concat("\n"));
	        out.append(lhs.toString(s+1).concat("\n"));
	        out.append(rhs.toString(s+1).concat("\n"));
	        
	        return out.toString();
	    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        llExpression l = (llExpression)lhs.getllRep(null, null);
        llExpression r = (llExpression)rhs.getllRep(null, null);
        
        return new llBinOp(l, r, operator);
    }
}