package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

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
}