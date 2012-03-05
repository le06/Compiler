package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

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