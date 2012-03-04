package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrMethodCallStmt extends IrInvokeStmt {
    private IrIdentifier method_name;
    private ArrayList<IrExpression> args;
}