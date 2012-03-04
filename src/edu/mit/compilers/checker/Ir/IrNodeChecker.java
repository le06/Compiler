package edu.mit.compilers.checker.Ir;

import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;

public class IrNodeChecker implements IrNodeVisitor {

	/*
	 * Auxilliary classes for implementing symbol tables.
	 */
	protected enum Type {
		VOID, INT, INT_ARRAY, BOOLEAN, BOOLEAN_ARRAY
	}

	protected class Env {
		private HashMap<String, Type> field_table;

		public Env() {
			field_table = new HashMap<String, Type>();
		}

		public HashMap<String, Type> getFieldTable() {
			return field_table;
		}
	}

	protected class MethodSignature {

		private final ArrayList<Type> param_types;
		private final ArrayList<String> param_ids;
		private final Type return_type;

		public MethodSignature(ArrayList<Type> param_types,
				ArrayList<String> param_ids, Type return_type) {
			this.param_types = param_types;
			this.param_ids = param_ids;
			this.return_type = return_type;
		}

		public ArrayList<Type> getParamTypes() {
			return param_types;
		}

		public ArrayList<String> getParamIds() {
			return param_ids;
		}

		public Type getReturnType() {
			return return_type;
		}
	}

	private Stack<Env> env_stack;
	private HashMap<String, MethodSignature> method_table;

	public IrNodeChecker() {
		env_stack = new Stack<Env>();
		env_stack.push(new Env());
		method_table = new HashMap<String, MethodSignature>();
	}

	/*
	 * Implemention of the IrNodeVisitor interface.
	 */
	@Override
	public void visit(IrClassDecl node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrGlobalDecl node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrArrayDecl node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrMethodDecl node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrVarDecl node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrBlock node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrAssignStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrPlusAssignStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrMinusAssignStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrContinueStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrBreakStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrReturnStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrWhileStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrForStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrIfStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrMethodCallStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrCalloutStmt node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrStringArg node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrExprArg node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrVarLocation node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrArrayLocation node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrLocationExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrMethodCallExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrCalloutExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrLiteralExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrBinopExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrUnopExpr node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrBinOperator node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrUnaryOperator node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrType node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrReturnType node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrIdentifier node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrIntLiteral node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrCharLiteral node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrBoolLiteral node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrStringLiteral node) {
		// TODO Auto-generated method stub

	}

}