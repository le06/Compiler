package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

// since Program is a token, a generated Ir should have a IrClassDecl node
// as its root.
public class IrClassDecl extends Ir {
    public void addMember(IrMemberDecl member) {
        members.add(member);
    }
    
    // order matters! need abstract members.
    private ArrayList<IrMemberDecl> members;
}