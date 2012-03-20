package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import edu.mit.compilers.codegen.ll.LLArrayDecl;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLNode;

// since Program is a token, a generated Ir should have a IrClassDecl node
// as its root.
public class IrClassDecl extends Ir {
    // order matters! need abstract members.
    private ArrayList<IrMemberDecl> members = new ArrayList<IrMemberDecl>();
	
    public void addMember(IrMemberDecl member) {
        members.add(member);
    }

	public ArrayList<IrMemberDecl> getMembers() {
		return members;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		for (IrMemberDecl m : members) {
			m.accept(v);
		}
		v.visit(this);
	}
	
	@Override
	public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
	    LLFile out = new LLFile();
	    LLMethodDecl nextMethod;
	    
	    for (IrMemberDecl m : members) {
	        if (m instanceof IrMethodDecl) {
	            nextMethod = (LLMethodDecl)m.getllRep(null, null);
	            if (nextMethod.getName().equals("main")) {
	                out.setMain(nextMethod);
	            } else {
	                out.addMethod(nextMethod);
	            }
	        } else if (m instanceof IrFieldDecl) {
	            for (IrGlobalDecl g : ((IrFieldDecl)m).getGlobals()) {
	                if (g instanceof IrArrayDecl) {
	                    out.addArrayDec((LLArrayDecl)g.getllRep(null, null));
	                } else if (g instanceof IrBaseDecl) {
	                    out.addGlobalDec((LLGlobalDecl)g.getllRep(null, null));
	                } else {
	                    throw new NotImplementedException();
	                }
	            }
	        } else {
	            throw new NotImplementedException();
	        }
	    }
	    
	    return out;
	}
	
	public String toString(int s) {
	    StringBuilder out = new StringBuilder();
	    for (int i = 0; i < s; i++) {
            out.append(" ");
        }
	    out.append("Program:\n");
	    
	    for (int i = 0; i < members.size(); i++) {
	        out.append(members.get(i).toString(s+1) + "\n");
	    }
	    
	    return out.toString();
	}
}