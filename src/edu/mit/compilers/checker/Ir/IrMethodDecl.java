package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrMethodDecl extends IrMemberDecl {
    private IrReturnType return_type;
    private IrIdentifier id;
    private ArrayList<IrVarDecl> args;
    private IrBlock block;
}