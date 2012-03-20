package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

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
		v.visit(this);
	}

	@Override
	public IrType getExprType(IrNodeChecker c) {
		return new IrType(IrType.Type.INT); // callouts always return int.
	}
	
	@Override
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("Callout:\n");
        out.append(function_name.toString(s+1).concat("\n"));
        for (IrCalloutArg d : args) {
            out.append(d.toString(s+1).concat("\n"));
        }
        
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLCallout c = new LLCallout(function_name.toString());
        for (IrCalloutArg arg : args) {
            c.addParam((LLExpression)arg.getllRep(null, null));
        }
        return c;
    }
}