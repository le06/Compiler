package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.Ir.IrNodeChecker.Type;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;

public class IrBinopExpr extends Ir implements IrExpression {
    public IrBinopExpr(IrBinOperator op, IrExpression left, IrExpression right) {
        operator = op;
        lhs = left;
        rhs = right;
    }
    
    private IrBinOperator operator;
    private IrExpression lhs;
    private IrExpression rhs;
    private IrType type;
    
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
				type = new IrType(IrType.Type.INT);
				break;
			// rel_op
			case LT:
			case GT:
			case LEQ:
			case GEQ:
			    type = new IrType(IrType.Type.BOOLEAN);
			    break;
			case EQ:
			case NEQ:
			    type = new IrType(IrType.Type.BOOLEAN);
			    break;
			case AND:
			case OR:
			    type = new IrType(IrType.Type.BOOLEAN);
			    break;
			}
			
		} else {
		    type = new IrType(IrType.Type.MIXED);
		}
		
		return type;
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLExpression l = (LLExpression)lhs.getllRep(null, null);
        LLExpression r = (LLExpression)rhs.getllRep(null, null);
        
        switch (type.myType) {
        case BOOLEAN:
            return new LLBinaryOp(l, r, operator, LLExpression.Type.BOOLEAN);
        case INT:
            return new LLBinaryOp(l, r, operator, LLExpression.Type.INT);
        case VOID:
            return new LLBinaryOp(l, r, operator, LLExpression.Type.VOID);
        default:
            return new LLNop();    
        }
    }
}