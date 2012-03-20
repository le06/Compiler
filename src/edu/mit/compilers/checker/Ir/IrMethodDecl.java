package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.checker.Ir.IrType.Type;
import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLReturn;

public class IrMethodDecl extends IrMemberDecl {
	private IrType return_type;
	private IrIdentifier id;
	private ArrayList<IrParameterDecl> params;
	private IrBlock block;
	
	public IrMethodDecl(IrType type, IrIdentifier name) {
	    return_type = type;
	    id = name;
	    params = new ArrayList<IrParameterDecl>();
	}
	
	public void addArg(IrParameterDecl arg) {
	    params.add(arg);
	}
	
	public void addBlock(IrBlock b) {
	    block = b;
	}
	
	public IrType getReturnType() {
		return return_type;
	}

	public IrIdentifier getId() {
		return id;
	}

	public ArrayList<IrParameterDecl> getParams() {
		return params;
	}

	public IrBlock getBlock() {
		return block;
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

    @Override
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("Method:\n");
        out.append(return_type.toString(s+1).concat("\n"));
        out.append(id.toString(s+1).concat("\n"));
        for (IrParameterDecl d : params) {
            out.append(d.toString(s+1).concat("\n"));
        }
        
        out.append(block.toString(s+1).concat("\n"));
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLEnvironment code = (LLEnvironment)block.getllRep(null, null);
        
        if (return_type.myType == Type.VOID) {
            LLReturn ret = new LLReturn(new LLIntLiteral(0));
            code.addNode(ret);
        }
        
        switch (return_type.myType) {
        case BOOLEAN:
            return new LLMethodDecl(id.getId(), LLExpression.Type.BOOLEAN, code);
        case INT:
            return new LLMethodDecl(id.getId(), LLExpression.Type.INT, code);
        case VOID:
            return new LLMethodDecl(id.getId(), LLExpression.Type.VOID, code);
        default:
            throw new RuntimeException("Cannot have a method with mixed type");
        }
        
    }
}