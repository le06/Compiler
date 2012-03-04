package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrCalloutStmt extends IrInvokeStmt {
	private IrStringLiteral function_name;
	private ArrayList<IrCalloutArg> args;
}