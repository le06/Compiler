package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrCalloutStmt extends IrInvokeStmt {
    private IrStringLiteral function_name;
    private ArrayList<IrCalloutArg> args = new ArrayList<IrCalloutArg>();
    
    public IrCalloutStmt (IrStringLiteral fn_name) {
        function_name = fn_name;
    }
    
    public void addArg(IrCalloutArg arg) {
        args.add(arg);
    }

	public IrStringLiteral getFunctionName() {
		return function_name;
	}

	public ArrayList<IrCalloutArg> getArgs() {
		return args;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		//v.visit(this);
	}

	@Override
	public IrType getExprType(IrNodeChecker c) {
		// TODO Auto-generated method stub
		return null;
	}
}