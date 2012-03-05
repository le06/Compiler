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
	
	private Stack<Env> env_stack;
	private HashMap<String, IrMethodDecl> method_table;
	private HashMap<String, Long> array_table;

	private Type current_type = Type.VOID;
	private boolean found_main_method = false;
	
	private Env getCurrentEnv() { return env_stack.peek(); }
	
	public IrNodeChecker() {
		env_stack = new Stack<Env>();
		env_stack.push(new Env());
		method_table = new HashMap<String, IrMethodDecl>();
		array_table = new HashMap<String, Long>();
	}
	
	
	
	/*
	 * Implemention of the IrNodeVisitor interface.
	 */

	public void visit(IrFieldDecl node) {
		current_type = determineType(node.getType());
		
		if (current_type == Type.VOID) {
			// TODO complain!
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
			// TODO complain!
		} else {
			// visitor will add id regardless of current_type's validity.
			field_table.put(id, current_type);
		}
	}

	@Override
	public void visit(IrArrayDecl node) { //TODO: check array size decl
		IrIdentifier id_node = node.getId();
		
		String id = id_node.getId();
		HashMap<String, Type> field_table = getCurrentEnv().getFieldTable();
		
		if (field_table.containsKey(id)) {
			// TODO complain!
		} else {
			Type type = Type.VOID_ARRAY;
			if (current_type == Type.INT) {
				type = Type.INT_ARRAY;
			} else if (current_type == Type.BOOLEAN) {
				type = Type.BOOLEAN_ARRAY;
			}
			
			long array_size;
			try {
				array_size = parseIntLiteral(node.getArraySize());
				if (array_size <= 0) {
					// TODO: complain!
				}
				else {
			// visitor will add id regardless of current_type's validity.
					field_table.put(id, type);
					array_table.put(id, array_size);
				}
			} catch (NumberFormatException e) {
				// TODO: complain!
				// intliteral out of bounds.
			}
			
		}	
	}
	
	@Override
	public void visit(IrMethodDecl node) {
		if (found_main_method) { // ignore methods defined after main().
			return; // TODO: possibly complain!
		}
		
		IrIdentifier id_node = node.getId();
		String id = id_node.getId();
		
		// variable calls and method calls are unambiguous.
		// hence, variable decls and method decls are also unambiguous.
		if (method_table.containsKey(id)) {
			// TODO complain!
		} else {
			method_table.put(id, node);
			if (id.equals("main")) {
				found_main_method = true;
				if (node.getParams().size() > 0) {
					// TODO complain!
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
	public void visit(IrBlock node) {
		// never called.
		// it's enough for var_decls and statements to accept this visitor.
	}
	
	@Override
	public void visit(IrVarDecl node) {
		current_type = determineType(node.getType());
		
		if (current_type == Type.VOID) {
			// TODO complain!
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
			// TODO complain!
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
		if (env == null) { // sanity check.
			// TODO: complain hard!
		}
		
		if (!legal_stmt) {
			// TODO: complain!
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
		if (env == null) { // sanity check.
			// TODO: complain hard!
		}
		
		if (!legal_stmt) {
			// TODO: complain!
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
		if (env == null) { // sanity check.
			// TODO: complain hard!
		}
		
		// what is this method's return value?
		IrType method_type_node = ((IrMethodDecl)current_body).getReturnType();
		IrType return_type_node = node.getReturnExpr().getType();
		Type method_type = determineType(method_type_node);
		Type return_type = determineType(return_type_node);

		if (method_type != return_type) {
			// TODO: complain! return type mismatch.
		}

	}
	
	@Override
	public void visit(IrWhileStmt node) {
		IrType type_node = node.getCondition().getType();
		Type type = determineType(type_node);
		if (type != Type.BOOLEAN) {
			// TODO: complain!
		}
		
		IrBlock block = node.getBlock();
		// create a new scope...
		Env method_env = new Env(getCurrentEnv(), node);
		env_stack.push(method_env); // enter the new env.
		block.accept(this);	// execute the block.
		env_stack.pop();	// exit the env.
	}

	@Override
	public void visit(IrForStmt node) {
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
	
	/*
	 * Helper functions.
	 */

	// if x < -9223372036854775808L or > 9223372036854775807L,
	// this method will throw an exception.
	private long parseIntLiteral(IrIntLiteral literal) 
		throws NumberFormatException {

		IrIntLiteral.Type type = literal.getType();
		String representation = literal.getRepresentation();

		if (type == IrIntLiteral.Type.DECIMAL) { // #####
			return Long.parseLong(representation);
		}
		else if (type == IrIntLiteral.Type.HEX) { // 0x####
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