package edu.mit.compilers.checker.Ir;

import java.math.BigInteger;
import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;

public class IrNodeChecker implements IrNodeVisitor {

	/*
	 * Auxilliary classes for implementing symbol tables.
	 */
	protected enum Type {
		MIXED, VOID, INT, BOOLEAN
	}

	protected class Env {
		private HashMap<String, Type> field_table;
		private HashMap<String, Integer> bp_offset_table;
		private Env previous_env;
		private Ir current_body;
		// i.e. the global scope, within a method, a for/while loop, etc...
		
		public Env() {
			field_table = new HashMap<String, Type>();
			bp_offset_table = new HashMap<String, Integer>();
			previous_env = null;
			current_body = null;
		}
		
		public Env(Env previous_env, Ir current_body) {
			field_table = new HashMap<String, Type>();
			bp_offset_table = new HashMap<String, Integer>();
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
		
		public HashMap<String, Integer> getOffsetTable() {
			return bp_offset_table;
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
	
	public boolean getError() { return error_flag; }
	
	private Stack<Env> env_stack;
	private HashMap<String, IrMethodDecl> method_table;
	private HashMap<String, Type> array_types;
	private HashMap<String, Long> array_sizes;

	private Type current_type = Type.VOID;
	private boolean found_main_method = false;
	private int currently_evaluating_expr = 0; // true if non-zero.
	
	// necessary for allocating locals during the codegen phase!
	private int local_count = 0;

	// count number of locals per method.
	private HashMap<String, Integer> locals_in_method =
		new HashMap<String, Integer>();
	
	public HashMap<String, Integer> getLocalCounts() {
		return locals_in_method;
	}
	
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
			return null; // array undefined.
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
		return null; // var undefined.
	}

	public int lookupOffset(String id) {
		Env current_env = getCurrentEnv();
		while (current_env != null) {
			Type type = current_env.getFieldTable().get(id);
			if (type != null && type != Type.VOID) {
				HashMap<String, Integer> offset_table =
					current_env.getOffsetTable();

				Integer offset = offset_table.get(id);
				if (offset != null) {
					return offset;
				}
			}
			current_env = current_env.getPreviousEnv();
		}
		return 0; // var undefined.
	}
	
	public IrType lookupMethodType(IrIdentifier id) {
		String name = id.getId();
		IrMethodDecl signature = method_table.get(name);
		
		if (signature == null) {
			return null; // method undefined.
		}
		
		Type return_type = determineType(signature.getReturnType());
		if (return_type == Type.VOID) {
			return new IrType(IrType.Type.VOID);
		} else if (return_type == Type.INT) {
			return new IrType(IrType.Type.INT);
		} else {
			return new IrType(IrType.Type.BOOLEAN);
		}
	}
	
	private boolean varIsDefined(String name) {
		Env current_env = getCurrentEnv();
		while (current_env != null) {
			// is the var in the current env?
			if (current_env.getFieldTable().containsKey(name)) {
				return true;
			}

			// else, go down the env stack.
			current_env = current_env.getPreviousEnv();
		}
		return false;
	}
	
	private boolean arrayIsDefined(String name) {
		return array_types.containsKey(name);
	}
	
	private boolean methodIsDefined(String name) {
		return method_table.containsKey(name);
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
			String message = "Invalid type for variable declaration: VOID";
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
			String message = "Duplicate base variable identifier: " + id;
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
			String message = "Duplicate array variable identifier: " + id;
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			long array_size;
			IrIntLiteral literal_node = node.getArraySize();
			try {
				//array_size = parseIntLiteral(literal_node);
			    array_size = literal_node.getIntRep();
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
					array_types.put(id, current_type);
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
		local_count = 0; // each method has its own %bp and locals.
		
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
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Duplicate method identifier: " + id;
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			method_table.put(id, node);
			if (id.equals("main")) {
				found_main_method = true;
				if (node.getParams().size() > 0) {
					IrParameterDecl param_node = node.getParams().get(0);
					error_flag = true;
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
		// don't forget to add the formal parameters to the symbol table,
		// and also check their consistency.
		ArrayList<IrParameterDecl> params = node.getParams();
		for (IrParameterDecl p : params) {
			String name = p.getId().getId();
			Type type = determineType(p.getType());
			if (method_env.getFieldTable().containsKey(name)) {
				error_flag = true;
				int line = p.getId().getLineNumber();
				int column = p.getId().getColumnNumber();
				String message = "Duplicate identifier in method args: " + name;
				System.out.println(errorPosMessage(line, column) + message);
			}
			else {
				method_env.getFieldTable().put(name, type);
			}
		}
		
		env_stack.push(method_env); // new env for each method.
		node.getBlock().accept(this);
		env_stack.pop();	// on exit, destroy the env.
		
		// record number of locals in this method.
		locals_in_method.put(id, local_count);
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
			String message = "Invalid type for variable declaration: void";
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
			String message = "Duplicate local variable identifier: " + id;
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			// visitor will add id regardless of current_type's validity.
			field_table.put(id, current_type);
			
			// the local needs to be allocated during codegen.
			local_count++;
			HashMap<String, Integer> offset_table = getCurrentEnv().
													getOffsetTable();
			offset_table.put(id, local_count);
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
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
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
		// make sure memory gets allocated for the loop counter.
		local_count++;
		for_env.getOffsetTable().put(id.getId(), local_count);
		
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
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
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
	
	
	// need to be careful with currently_evaluating_expr, so that we can tell
	// whether a method call is part of an expression or not.
	@Override
	public void visit(IrMethodCallStmt node) {
		IrIdentifier method_name = node.getMethodName();
		ArrayList<IrExpression> args = node.getArgs();

		// if the method is undefined...
		if (!methodIsDefined(method_name.getId())) {
			error_flag = true;
			int line = method_name.getLineNumber();
			int column = method_name.getColumnNumber();
			String message = "Cannot call to undefined method: " + method_name.getId();
			System.out.println(errorPosMessage(line, column) + message);
			
			return;
		}
		IrMethodDecl called_method = method_table.get(method_name.getId());
		ArrayList<IrParameterDecl> params = called_method.getParams();
		
		// check that the args are well-formed.
		for (IrExpression arg : node.getArgs()) {
			arg.accept(this);
		}
		
		// if the signatures don't match...
		if (args.size() != params.size()) {
			error_flag = true;
			int line = args.get(0).getLineNumber();
			int column = args.get(0).getColumnNumber();
			String message = "Mismatching number of arguments";
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			for (int i = 0; i < args.size(); i++) {
				IrType arg_type = args.get(i).getExprType(this);
				IrType param_type = params.get(i).getType();
				
				if (arg_type.myType != param_type.myType) {
					error_flag = true;
					int line = param_type.getLineNumber();
					int column = param_type.getColumnNumber();
					String message = "Mismatching types of arguments";
					System.out.println(errorPosMessage(line, column) + message);

					break;
				}
			}
		}
		
		// if the method call is part of an expression...
		Type return_type = determineType(called_method.getReturnType());
		if (currently_evaluating_expr > 0 && return_type == Type.VOID) {
			error_flag = true;
			int line = node.getMethodName().getLineNumber();
			int column = node.getMethodName().getColumnNumber();
			String message = "Void methods cannot be part of int or boolean expressions";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
	}

	@Override
	public void visit(IrCalloutStmt node) {
		currently_evaluating_expr++;
		// check that the args are well-formed.
		for (IrCalloutArg arg : node.getArgs()) {
			arg.accept(this);
		}
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrAssignStmt node) {
		currently_evaluating_expr++;
		
		IrLocation loc = node.getLeft();
		IrExpression expr = node.getRight();
		// check that loc and expr are well-formed.
		loc.accept(this);
		expr.accept(this);
		// check that the types match up.
		Type loc_type = determineType(loc.getExprType(this));
		Type expr_type = determineType(expr.getExprType(this));
		
		if (loc_type == Type.MIXED || loc_type == Type.VOID ||
			expr_type == Type.MIXED || expr_type == Type.VOID) {
			error_flag = true;
			int line = loc.getId().getLineNumber();
			int column = loc.getId().getColumnNumber();
			String message = "Assignment operands have invalid types";
			System.out.println(errorPosMessage(line, column) + message);
		} else if (loc_type != expr_type) {
			error_flag = true;
			int line = loc.getLineNumber();
			int column = loc.getColumnNumber();
			String message = "Assignment operands have mismatching types";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrPlusAssignStmt node) {
		currently_evaluating_expr++;

		IrLocation loc = node.getLeft();
		IrExpression expr = node.getRight();
		// check that loc and expr are well-formed.
		loc.accept(this);
		expr.accept(this);
		// check that the types match up.
		Type loc_type = determineType(loc.getExprType(this));
		Type expr_type = determineType(expr.getExprType(this));
		
		if (loc_type != Type.INT) {
			error_flag = true;
			int line = loc.getId().getLineNumber();
			int column = loc.getId().getColumnNumber();
			String message = "LHS of plus assignment must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		if (expr_type != Type.INT) {
			error_flag = true;
			int line = expr.getLineNumber();
			int column = expr.getColumnNumber();
			String message = "RHS of plus assignment must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrMinusAssignStmt node) {
		currently_evaluating_expr++;

		IrLocation loc = node.getLeft();
		IrExpression expr = node.getRight();
		// check that loc and expr are well-formed.
		loc.accept(this);
		expr.accept(this);
		// check that the types match up.
		Type loc_type = determineType(loc.getExprType(this));
		Type expr_type = determineType(expr.getExprType(this));
		
		if (loc_type != Type.INT) {
			error_flag = true;
			int line = loc.getId().getLineNumber();
			int column = loc.getId().getColumnNumber();
			String message = "LHS of minus assignment must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		if (expr_type != Type.INT) {
			error_flag = true;
			int line = expr.getLineNumber();
			int column = expr.getColumnNumber();
			String message = "RHS of minus assignment must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrVarLocation node) {
		IrIdentifier id_node = node.getId();
		if (!varIsDefined(id_node.getId())) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Variable identifier is undefined: " + id_node.getId();
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			// var is defined, set node's bp offset!
			int offset = lookupOffset(id_node.getId());
			node.setBpOffset(offset);
		}
	}

	@Override
	public void visit(IrArrayLocation node) {
		IrIdentifier id_node = node.getId();
		if (!arrayIsDefined(id_node.getId())) {
			error_flag = true;
			int line = id_node.getLineNumber();
			int column = id_node.getColumnNumber();
			String message = "Array identifier is undefined: " + id_node.getId();
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		IrExpression index_node = node.getIndex();
		Type index_type = determineType(index_node.getExprType(this));

		if (index_type == null) {
			error_flag = true;
			int line = index_node.getLineNumber();
			int column = index_node.getColumnNumber();
			String message = "Array index is undefined";
			System.out.println(errorPosMessage(line, column) + message);
		} else if (index_type != Type.INT) {
			error_flag = true;
			int line = index_node.getLineNumber();
			int column = index_node.getColumnNumber();
			String message = "Array index must be int type";
			System.out.println(errorPosMessage(line, column) + message);
		}
	}
	
	@Override
	public void visit(IrBinopExpr node) {
		currently_evaluating_expr++;

		IrExpression lhs = node.getLeft();
		IrExpression rhs = node.getRight();
		// check that lhs and rhs are well-formed.
		lhs.accept(this);
		rhs.accept(this);
		
		IrType type_node = node.getExprType(this);
		Type type = determineType(type_node);

		if (type == Type.MIXED) {
			error_flag = true;
			int line = lhs.getLineNumber();
			int column = lhs.getColumnNumber();
			String message = "Mismatching types of operands in expression";
			System.out.println(errorPosMessage(line, column) + message);
			
			return;
		} else {
			IrBinOperator op = node.getOperator();
			
			IrType lhs_type_node = lhs.getExprType(this);
			IrType rhs_type_node = rhs.getExprType(this);
			Type lhs_type = determineType(lhs_type_node);
			Type rhs_type = determineType(rhs_type_node);
			
			switch (op) {
			// arith_op
			case PLUS:
			case MINUS:
			case MUL:
			case DIV:
			case MOD:
			// rel_op
			case LT:
			case GT:
			case LEQ:
			case GEQ:
				if (lhs_type != Type.INT || rhs_type != Type.INT) {
					error_flag = true;
					int line = lhs.getLineNumber();
					int column = lhs.getColumnNumber();
					String message = "Expression operands must have int type";
					System.out.println(errorPosMessage(line, column) + message);
				}
				break;
			// eq_op
			case EQ:
			case NEQ:
				if (lhs_type == Type.MIXED || lhs_type == Type.VOID ||
					rhs_type == Type.MIXED || rhs_type == Type.VOID) {
					error_flag = true;
					int line = lhs.getLineNumber();
					int column = lhs.getColumnNumber();
					String message = "Expression operands have invalid types";
					System.out.println(errorPosMessage(line, column) + message);
				} else {
					error_flag = true;
					int line = lhs.getLineNumber();
					int column = lhs.getColumnNumber();
					String message = "Expression operands have mismatching types";
					System.out.println(errorPosMessage(line, column) + message);
				}
				break;
			// cond_op
			case AND:
			case OR:
				if (lhs_type != Type.BOOLEAN || rhs_type != Type.BOOLEAN) {
					error_flag = true;
					int line = lhs.getLineNumber();
					int column = lhs.getColumnNumber();
					String message = "Expression operands must have boolean type";
					System.out.println(errorPosMessage(line, column) + message);
				}
				break;
			}
			
		}
		
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrUnopExpr node) {
		currently_evaluating_expr++;

		IrUnaryOperator op = node.getOperator();
		
		IrExpression expr = node.getExpr();
		Type expr_type = determineType(expr.getExprType(this));

		if (expr_type == Type.MIXED || expr_type == Type.VOID) {
			error_flag = true;
			int line = expr.getLineNumber();
			int column = expr.getColumnNumber();
			String message = "Unary expression has invalid type";
			System.out.println(errorPosMessage(line, column) + message);
		} else if ((expr_type == Type.INT && op == IrUnaryOperator.NOT) || 
				(expr_type == Type.BOOLEAN && op == IrUnaryOperator.MINUS)) {
			error_flag = true;
			int line = expr.getLineNumber();
			int column = expr.getColumnNumber();
			String message = "Unary operator and expression have mismatching types";
			System.out.println(errorPosMessage(line, column) + message);
		}
		
		currently_evaluating_expr--;
	}

	@Override
	public void visit(IrIntLiteral node) {
		try {
			node.getIntRep();
		} catch (NumberFormatException e) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "IntLiteral out of bounds";
			System.out.println(errorPosMessage(line, column) + message);
		}	
	}
	
	@Override
	public void visit(IrIdentifier node) {
		String id = node.getId();
		if (!varIsDefined(id)) {
			error_flag = true;
			int line = node.getLineNumber();
			int column = node.getColumnNumber();
			String message = "Variable identifier is undefined: " + node.getId();
			System.out.println(errorPosMessage(line, column) + message);
		} else {
			// var is defined, set node's bp offset!
			int offset = lookupOffset(id);
			node.setBpOffset(offset);
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
	// this method should throw an exception.
	
	
	private Type determineType(IrType type) {
		if (type == null) {
			return null;
		}
		
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