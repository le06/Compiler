package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrMethodCallStmt extends IrInvokeStmt {
    public IrMethodCallStmt(IrIdentifier name) {
        method_name = name;
        args = new ArrayList<IrExpression>();
    }
    
    public void addArg(IrExpression arg) {
        args.add(arg);
    }
    
	private IrIdentifier method_name;
	private ArrayList<IrExpression> args;
	
	public IrIdentifier getMethodName() {
		return method_name;
	}

	public ArrayList<IrExpression> getArgs() {
		return args;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);		
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupMethodType(method_name);
	}
	
	@Override
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("Method Call:\n");
        out.append(method_name.toString(s+1).concat("\n"));
        for (IrExpression d : args) {
            out.append(d.toString(s+1).concat("\n"));
        }
        
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLMethodCall out = new LLMethodCall(method_name.getId());
        
        for (IrExpression arg : args) {
            out.addParam((LLExpression)arg.getllRep(null, null));
        }
        
        return out;
    }
}