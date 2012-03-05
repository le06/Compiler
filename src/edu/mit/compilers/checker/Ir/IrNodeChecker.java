package edu.mit.compilers.checker.Ir;

import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;

public class IrNodeChecker implements IrNodeVisitor {

	/*
	 * Auxilliary classes for implementing symbol tables.
	 */
	protected enum Type {
		MIXED, VOID, VOID_ARRAY, INT, INT_ARRAY, BOOLEAN, BOOLEAN_ARRAY
	}

	protected class Env {
		private HashMap<String, Type> field_table;
		private Env previous_env;
		private Ir current_body;
		// i.e. the global scope, within a method, a for/while loop, etc...
		
		public Env() {
			field_table = new HashMap<String, Type>();
			previous_env = null;
			current_body = null;
		}
		
		public Env(Env previous_env, Ir current_body) {
			field_table = new HashMap<String, Type>();
			this.previous_env = previous_env;
			this.current_body = current_body;
		}
		
		public Env getPreviousEnv() {
			return previous_env;
		}

		public Ir getCurrentBody() {
			return current_body;
		}
		
		public HashMap<String, Type> getFieldTable() {
			return field_table;
		}
	}

	/*
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
	*/

	private boolean error_flag = false;
	
	public boolean wasError() { return error_flag; }
	
	private Stack<Env> env_stack;
	private HashMap<String, IrMethodDecl> method_table;
	private HashMap<String, Type> array_types;
	private HashMap<String, Long> array_sizes;

	private Type current_type = Type.VOID;
	private boolean found_main_method = false;
	
	private Env getCurrentEnv() { return env_stack.peek(); }
	
	public IrNodeChecker() {
		env_stack = new Stack<Env>();
		env_stack.push(new Env());
		method_table = new HashMap<String, IrMethodDecl>();
		array_types = new HashMap<String, Type>();
		array_sizes = new HashMap<String, Long>();
	}
	
	// null type means a variable or array was declared incorrectly,
	// or simply wasn't declared.
	public IrType lookupArrayType(IrIdentifier id) {
		String name = id.getId();
		Type type = array_types.get(name);
		if (type == null || type == Type.VOID) {
			return null;
		} else if (type == Type.INT) {
			return new IrType(IrType.Type.INT);
		} else {
			return new IrType(IrType.Type.BOOLEAN);
		}
	}
	
	public IrType lookupVarType(IrIdentifier id) {
		String name = id.getId();
		Env current_env = getCurrentEnv();
		while (current_env != null) {
			Type type = current_env.getFieldTable().get(name);
			if (type != null && type != Type.VOID) {
				if (type == Type.INT) {
					return new IrType(IrType.Type.INT);
				} else {
					return new IrType(IrType.Type.BOOLEAN);
				}
			}
			current_env = current_env.getPreviousEnv();
		}
		return null;
	}

	private boolean varIsDefined(String name) {
		Env current_env = getCurrentEnv();
		while (current_env != null) {
			if (current_env.getFieldTable().containsKey(name)) {
				return true;
			}			
			current_env = current_env.getPreviousEnv();
		}
		return false;
	}
	
	private boolean arrayIsDefined(String name) {
		return array_types.containsKey(name);
	}
	
	/*
	 * Implemention of the IrNodeVisitor interface.
	 */

	public void visit(IrClassDecl node) {
		if (!found_main_method) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "No main method";
			System.out.println(errorPosMessage(line, column) + message);
		}
	}
	
	public void visit(IrFieldDecl node) {
		IrType type_node = node.getType();
		current_type = determineType(type_node);
		// this should be caught by the parser!
		if (current_type == Type.VOID) {
			error_flag = true;
			int line = type_node.getLineNumber();
			int column = type_node.getColumnNumber();
			String message = "Invalid type for variable declaration";
			System.out.println(errorPosMessage(line, column) + message);
		}

		ArrayList<IrGlobalDecl> globals = node.getGlobals();
		for (IrGlobalDecl g : globals) {
			g.accept(this);
		}
	}
	
	// note that the parser rejects any fields that are declared after
	// method declarations; hence we do not worry about method ids.
	@Override
	public void visit(IrBaseDecl node) {
		IrIdentifier id_node = node.getId();
		
		String id = id_node.getId();
		HashMap<String, Type> field_table = getCurrentEnv().getFieldTable();
		
		if (field_table.containsKey(id)) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Duplicate base variable identifier";
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			// visitor will add id regardless of current_type's validity.
			field_table.put(id, current_type);
		}
	}

	@Override
	public void visit(IrArrayDecl node) {
		IrIdentifier id_node = node.getId();
		
		String id = id_node.getId();
		if (array_types.containsKey(id)) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Duplicate array variable identifier";
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			
			Type type = Type.VOID_ARRAY;
			if (current_type == Type.INT) {
				type = Type.INT_ARRAY;
			} else if (current_type == Type.BOOLEAN) {
				type = Type.BOOLEAN_ARRAY;
			}
			
			long array_size;
			IrIntLiteral literal_node = node.getArraySize();
			try {
				array_size = parseIntLiteral(literal_node);
				if (array_size <= 0) {
					error_flag = true;
					int line = literal_node.getLineNumber();
					int column = literal_node.getColumnNumber();
					String message = "Array size must be non-negative";
					System.out.println(errorPosMessage(line, column) + message);
				}
				else {
				// visitor will add id regardless of current_type's validity.
				// note that defining both a and a[] is legal, due to the
				// semantics of Decaf!
					array_types.put(id, type);
					array_sizes.put(id, array_size);
				}
			} catch (NumberFormatException e) {
				error_flag = true;
				int line = literal_node.getLineNumber();
				int column = literal_node.getColumnNumber();
				String message = "IntLiteral out of bounds";
				System.out.println(errorPosMessage(line, column) + message);
			}	
			
		}
		
	}
	
	@Override
	public void visit(IrMethodDecl node) {
		if (found_main_method) { // ignore methods defined after main().
			IrType type_node = node.getReturnType();
			int line = type_node.getLineNumber();
			int column = type_node.getColumnNumber();
			String message = "Methods defined after main can never be referenced";
			System.out.println(warningPosMessage(line, column) + message);
			return;
		}
		
		IrIdentifier id_node = node.getId();
		String id = id_node.getId();
		
		// variable calls and method calls are unambiguous.
		// hence, variable decls and method decls are also unambiguous.
		if (method_table.containsKey(id)) {
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Duplicate method identifier";
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			method_table.put(id, node);
			if (id.equals("main")) {
				found_main_method = true;
				if (node.getParams().size() > 0) {
					IrParameterDecl param_node = node.getParams().get(0);
					int line = param_node.getLineNumber();
					int column = param_node.getColumnNumber();
					String message = "main() method cannot have any arguments";
					System.out.println(errorPosMessage(line, column) + message);
				}
			}
		}
		
		// check contents of method no matter what, assuming that main()
		// hasn't been found yet.
		Env method_env = new Env(getCurrentEnv(), node);
		env_stack.push(method_env); // new env for each method.
		node.getBlock().accept(this);
		env_stack.pop();	// on exit, destroy the env.
	}
	
	/*
	 * Visiting method contents!
	 */
	
	@Override
	public void visit(IrVarDecl node) {
		IrType type_node = node.getType();
		current_type = determineType(type_node);
		// again, this should be caught by the parser
		if (current_type == Type.VOID) {
			error_flag = true;
			int line = type_node.getLineNumber();
			int column = type_node.getColumnNumber();
			String message = "Invalid type for variable declaration";
			System.out.println(errorPosMessage(line, column) + message);
		}

		ArrayList<IrLocalDecl> locals = node.getLocals();
		for (IrLocalDecl l : locals) {
			l.accept(this);
		}
	}
	
	@Override
	public void visit(IrLocalDecl node) {
		IrIdentifier id_node = node.getId();
		
		String id = id_node.getId();
		HashMap<String, Type> field_table = getCurrentEnv().getFieldTable();
		
		// i.e. if the id is declared twice IN THE SAME SCOPE.
		// note that since method calls and decls can be distinguished,
		// method ids are never shadowed!
		if (field_table.containsKey(id)) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Duplicate local variable identifier";
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			// visitor will add id regardless of current_type's validity.
			field_table.put(id, current_type);
		}
	}

	@Override
	public void visit(IrBlockStmt node) {
		IrBlock block = node.getBlock();
		// create a new scope...
		Env method_env = new Env(getCurrentEnv(), node);
		env_stack.push(method_env); // enter the new env.
		block.accept(this);	// execute the block.
		env_stack.pop();	// exit the env.
	}
	
	@Override
	public void visit(IrContinueStmt node) {
		// recursively check through the environments to see if the stmt is
		// contained in the body of a for or while stmt.
		Env env = getCurrentEnv();
		Ir current_body = env.getCurrentBody();
		boolean legal_stmt = false;
		
		while (env != null && !(current_body instanceof IrMethodDecl)) {
			if ((current_body instanceof IrForStmt) ||
				(current_body instanceof IrWhileStmt)) {
				legal_stmt = true;
				break;
			}
			env = env.getPreviousEnv();
			current_body = env.getCurrentBody();
		}
		if (env == null) { // sanity check. this should never be true!
			// TODO: complain hard!
			error_flag = true;
			System.out.println(node.getLineNumber() + "," + node.getColumnNumber());
		}
		
		if (!legal_stmt) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "Continue statements must be placed in the body of a for or while statement";
			System.out.println(errorPosMessage(line, column) + message);
		}
	}

	@Override
	public void visit(IrBreakStmt node) {
		// recursively check through the environments to see if the stmt is
		// contained in the body of a for or while stmt.
		Env env = getCurrentEnv();
		Ir current_body = env.getCurrentBody();
		boolean legal_stmt = false;
		
		while (env != null && !(current_body instanceof IrMethodDecl)) {			
			if ((current_body instanceof IrForStmt) ||
				(current_body instanceof IrWhileStmt)) {
				legal_stmt = true;
				break;
			}
			env = env.getPreviousEnv();
			current_body = env.getCurrentBody();
		}
		if (env == null) { // sanity check. this should never be true!
			// TODO: complain hard!
			error_flag = true;
			System.out.println(node.getLineNumber() + "," + node.getColumnNumber());
		}
		
		if (!legal_stmt) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "Break statements must be placed in the body of a for or while statement";
			System.out.println(errorPosMessage(line, column) + message);
		}
	}
	
	@Override
	public void visit(IrReturnStmt node) {
		Env env = getCurrentEnv();
		Ir current_body = env.getCurrentBody();
		while (env != null && !(current_body instanceof IrMethodDecl)) {
			env = env.getPreviousEnv();
			current_body = env.getCurrentBody();
		}
		if (env == null) { // sanity check. this should never be true!
			// TODO: complain hard!
			error_flag = true;
			System.out.println(node.getLineNumber() + "," + node.getColumnNumber());
		}

		IrExpression return_expr = node.getReturnExpr();
		// what is this method's return value?
		IrType method_type_node = ((IrMethodDecl)current_body).getReturnType();
		Type method_type = determineType(method_type_node);
		
		if (method_type == Type.VOID) {
			if (return_expr != null) {
				error_flag = true;
				int line = node.getLineNumber();
				int column = node.getColumnNumber();
				String message = "Void-type methods cannot return expressions";
				System.out.println(errorPosMessage(line, column) + message);
			}
		}
		else {
			if (return_expr == null) {
				error_flag = true;
				int line = node.getLineNumber();
				int column = node.getColumnNumber();
				String message = "Non-void-type method missing return expression";
				System.out.println(errorPosMessage(line, column) + message);
			}
			
			return_expr.accept(this); // check that the expr is well-formed.
			if (method_type != determineType(return_expr.getExprType(this))) {
				error_flag = true;
				int line = node.getLineNumber();
				int column = node.getColumnNumber();
				String message = "Type mismatch in return expression";
				System.out.println(errorPosMessage(line, column) + message);
			}
		}

	}
	
	@Override
	public void visit(IrWhileStmt node) {
		IrExpression condition = node.getCondition();
		
		// while condition well-formed?
		condition.accept(this);
		IrType type_node = condition.getExprType(this);
		Type type = determineType(type_node);
		if (type != Type.BOOLEAN) {
			error_flag = true;
			int line = type_node.getLineNumber();
			int column = type_node.getColumnNumber();
			String message = "While loop condition must have a boolean type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		IrBlock block = node.getBlock();
		// create a new scope...
		Env while_env = new Env(getCurrentEnv(), node);
		env_stack.push(while_env); // enter the new env.
		block.accept(this);	// execute the block.
		env_stack.pop();	// exit the env.
	}

	@Override
	public void visit(IrForStmt node) {
		IrExpression startExpr = node.getStartValue();
		IrExpression stopExpr = node.getStopValue();

		// are the exprs well-formed?
		startExpr.accept(this);
		stopExpr.accept(this);
		if (determineType(startExpr.getExprType(this)) != Type.INT) {
			error_flag = true;
			int line = startExpr.getLineNumber();
			int column = startExpr.getColumnNumber();
			String message = "For loop initialization expr must have int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		if (determineType(stopExpr.getExprType(this)) != Type.INT) {
			error_flag = true;
			int line = stopExpr.getLineNumber();
			int column = stopExpr.getColumnNumber();
			String message = "For loop end condition expr must have int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		IrBlock block = node.getBlock();
		IrIdentifier id = node.getCounter();
		Env for_env = new Env(getCurrentEnv(), node);
		// add the counter to the for loop env.
		for_env.getFieldTable().put(id.getId(), Type.INT);
		
		env_stack.push(for_env); // enter the new env.		
		block.accept(this);      // execute the block.
		env_stack.pop();         // exit the env.
	}
	
	@Override
	public void visit(IrIfStmt node) {
		IrExpression condition = node.getCondition();
		// if condition well-formed?
		condition.accept(this);
		IrType type_node = condition.getExprType(this);
		Type type = determineType(type_node);
		if (type != Type.BOOLEAN) {
			error_flag = true;
			int line = type_node.getLineNumber();
			int column = type_node.getColumnNumber();
			String message = "If loop condition must have a boolean type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		IrBlock true_block = node.getTrueBlock();
		IrBlock false_block = node.getFalseBlock();
		
		Env true_env = new Env(getCurrentEnv(), node);
		env_stack.push(true_env); // enter the new env.
		true_block.accept(this);  // execute the block.
		env_stack.pop();		  // exit the env.
		
		if (false_block != null) {
			Env false_env = new Env(getCurrentEnv(), node);
			env_stack.push(false_env); // enter the new env.
			false_block.accept(this);  // execute the block.
			env_stack.pop();           // exit the env.
		}

	}
	
	
	
	@Override
	public void visit(IrMethodCallStmt node) {
		IrIdentifier method_name = node.getMethodName();
		ArrayList<IrExpression> args = node.getArgs();

		// if the method is undefined...
		if (!method_table.containsKey(method_name.getId())) {
			// TODO: complain!

		}
		
		
	}

	@Override
	public void visit(IrCalloutStmt node) {
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
	public void visit(IrStringArg node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrExprArg node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IrVarLocation node) {
		IrIdentifier id_node = node.getId();
		if (!varIsDefined(id_node.getId())) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Variable identifier is undefined";
			System.out.println(errorPosMessage(line, column) + message);
		}
	}

	@Override
	public void visit(IrArrayLocation node) {
		IrIdentifier id_node = node.getId();
		if (!arrayIsDefined(id_node.getId())) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Array identifier is undefined";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		IrExpression index_node = node.getIndex();
		Type index_type = determineType(index_node.getExprType(this));

		if (index_type != Type.INT) {
			error_flag = true;
			int line = index_node.getLineNumber();
			int column = index_node.getColumnNumber();
			String message = "Array index must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
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
	public void visit(IrIntLiteral node) {
		try {
			parseIntLiteral(node);
		} catch (NumberFormatException e) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "IntLiteral out of bounds";
			System.out.println(errorPosMessage(line, column) + message);
		}	
	}
	
	/*
	 * Helper functions.
	 */
	
	private String errorPosMessage(int line, int column) {
		return "Error at line " + line + ", column " + column + ": ";
	}
	
	private String warningPosMessage(int line, int column) {
		return "Warning at line " + line + ", column " + column + ": ";
	}
	
	// if x < -9223372036854775808L or > 9223372036854775807L,
	// this method will throw an exception.
	private long parseIntLiteral(IrIntLiteral literal) 
		throws NumberFormatException {

		IrIntLiteral.NumType num_type = literal.getNumType();
		String representation = literal.getRepresentation();

		if (num_type == IrIntLiteral.NumType.DECIMAL) { // #####
			return Long.parseLong(representation);
		}
		else if (num_type == IrIntLiteral.NumType.HEX) { // 0x####
			return Long.parseLong(representation.substring(2), 16);
		}
		else { // 0b####
			return Long.parseLong(representation.substring(2), 2);
		}
	}
	
	private Type determineType(IrType type) {
		if (type.myType == IrType.Type.INT) {
			return Type.INT;
		} else if (type.myType == IrType.Type.BOOLEAN) {
			return Type.BOOLEAN;
		} else if (type.myType == IrType.Type.VOID) {
			return Type.VOID;
		} else {
			return Type.MIXED; // only for binary exprs. invalid type.
		}
	}
	
}