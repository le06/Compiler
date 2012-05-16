package edu.mit.compilers.checker;

import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;

import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.checker.Ir.IrArrayDecl;
import edu.mit.compilers.checker.Ir.IrArrayLocation;
import edu.mit.compilers.checker.Ir.IrAssignStmt;
import edu.mit.compilers.checker.Ir.IrBaseDecl;
import edu.mit.compilers.checker.Ir.IrBinOperator;
import edu.mit.compilers.checker.Ir.IrBinopExpr;
import edu.mit.compilers.checker.Ir.IrBlock;
import edu.mit.compilers.checker.Ir.IrBlockStmt;
import edu.mit.compilers.checker.Ir.IrBoolLiteral;
import edu.mit.compilers.checker.Ir.IrBreakStmt;
import edu.mit.compilers.checker.Ir.IrCalloutArg;
import edu.mit.compilers.checker.Ir.IrCalloutStmt;
import edu.mit.compilers.checker.Ir.IrClassDecl;
import edu.mit.compilers.checker.Ir.IrContinueStmt;
import edu.mit.compilers.checker.Ir.IrExpression;
import edu.mit.compilers.checker.Ir.IrFieldDecl;
import edu.mit.compilers.checker.Ir.IrForStmt;
import edu.mit.compilers.checker.Ir.IrGlobalDecl;
import edu.mit.compilers.checker.Ir.IrIdentifier;
import edu.mit.compilers.checker.Ir.IrIfStmt;
import edu.mit.compilers.checker.Ir.IrIntLiteral;
import edu.mit.compilers.checker.Ir.IrLocalDecl;
import edu.mit.compilers.checker.Ir.IrLocation;
import edu.mit.compilers.checker.Ir.IrMemberDecl;
import edu.mit.compilers.checker.Ir.IrMethodCallStmt;
import edu.mit.compilers.checker.Ir.IrMethodDecl;
import edu.mit.compilers.checker.Ir.IrMinusAssignStmt;
import edu.mit.compilers.checker.Ir.IrNodeVisitor;
import edu.mit.compilers.checker.Ir.IrParameterDecl;
import edu.mit.compilers.checker.Ir.IrPlusAssignStmt;
import edu.mit.compilers.checker.Ir.IrReturnStmt;
import edu.mit.compilers.checker.Ir.IrStatement;
import edu.mit.compilers.checker.Ir.IrType;
import edu.mit.compilers.checker.Ir.IrUnaryOperator;
import edu.mit.compilers.checker.Ir.IrUnopExpr;
import edu.mit.compilers.checker.Ir.IrVarDecl;
import edu.mit.compilers.checker.Ir.IrVarLocation;
import edu.mit.compilers.checker.Ir.IrWhileStmt;
import edu.mit.compilers.checker.Ir.IrType.Type;

public class SemanticChecker implements IrNodeVisitor {
    
    /*
     * Auxilliary classes for implementing symbol tables.
     */
    protected enum Type {
    	MIXED, VOID, INT, BOOLEAN
    }
    
    protected class Env {
    	// the set of variables defined in this scope.
    	private HashMap<String, Type> field_table;
    	
    	// mapping of locals to distinct symbols - necessary for codegen.
    	private HashMap<String, String> symbol_table;
    	
    	// the parent environment. if previous_env is null, this env is the global scope.
    	private Env previous_env;
    	
    	// the code contained in this env: global scope, method scope, inside a for/while loop, etc
    	private Ir current_body;
    	
    	// Constructor for the global scope.
    	public Env() {
    		field_table = new HashMap<String, Type>();
    		symbol_table = new HashMap<String, String>();
    		previous_env = null;
    		current_body = null;
    	}
    	
    	// Constructor for local scopes.
    	public Env(Env previous_env, Ir current_body) {
    		field_table = new HashMap<String, Type>();
    		symbol_table = new HashMap<String, String>();
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
    	
    	public HashMap<String, String> getSymbolTable() {
    		return symbol_table;
    	}
    }
    
    private boolean error_flag = false;
    public boolean getError() { return error_flag; }
    
    private Stack<Env> env_stack;
    private HashMap<String, IrMethodDecl> method_table;
    // arrays are guaranteed to be global; these are stored distinct from the envs.
    private HashMap<String, Type> array_types;
    private HashMap<String, Long> array_sizes;
    private HashMap<String, String> array_symbols;
    
    private Type current_type = null;
    private boolean found_main_method = false;
    private int currently_evaluating_expr = 0; // "true" if non-zero. like a semaphore.
    
    // these counters are incremented each time a new var is assigned to a symbol.
    private int global_counter = 1; // g##
    private int array_counter = 1;  // a##
    private int local_counter = 1;  // v##
    
    private Env getCurrentEnv() { return env_stack.peek(); }
    
    public SemanticChecker() {
    	env_stack = new Stack<Env>();
    	env_stack.push(new Env());
    	
    	method_table = new HashMap<String, IrMethodDecl>();
    	array_types = new HashMap<String, Type>();
    	array_sizes = new HashMap<String, Long>();
    	array_symbols = new HashMap<String, String>();
    }
    
    /*
     * Methods for looking up environment variables, given an identifier.
     * 
     * A return value of null implies a variable or array was declared
     * incorrectly or wasn't declared in the first place.
     */
    
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
    
    public long lookupArraySize(IrIdentifier id) {
    	String name = id.getId();
    	Long size = array_sizes.get(name);
    	if (size == null) {
    		return 0; // array undefined.
    	} else {
    		return size;
    	}
    }
    
    public IrType lookupVarType(IrIdentifier id) {
    	String name = id.getId();
    	Env current_env = getCurrentEnv();
    	while (current_env != null) {
    		Type type = current_env.getFieldTable().get(name);
    		// if the variable was declared properly...
    		if (type != null && type != Type.VOID) {				
    			if (type == Type.INT) {
    				return new IrType(IrType.Type.INT);
    			} else {
    				return new IrType(IrType.Type.BOOLEAN);
    			}
    		}
    		current_env = current_env.getPreviousEnv();
    	}
    	return null; // var is undefined.
    }
    
    public String lookupVarSymbol(IrIdentifier id) {
        String name = id.getId();
    	Env current_env = getCurrentEnv();
    	while (current_env != null) {
    		Type type = current_env.getFieldTable().get(name);
    		if (type != null && type != Type.VOID) {
    		    // every defined variable must should have an associated symbol.
    		    // a sanity check might be nice...
    			return current_env.getSymbolTable().get(name);
    		}
    		current_env = current_env.getPreviousEnv();
    	}
    	return null; // var is undefined. handle this case if necessary.
    }
    
    public IrType lookupMethodType(IrIdentifier id) {
    	String name = id.getId();
    	IrMethodDecl signature = method_table.get(name);
    	
    	if (signature == null) {
    		return null; // method is undefined.
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
    
    // Methods for checking whether a given name is defined.
    
    private boolean arrayIsDefined(String name) {
        return array_types.containsKey(name);
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
    
    private boolean methodIsDefined(String name) {
    	return method_table.containsKey(name);
    }
    
    /*
     * Implementation of the IrNodeVisitor interface.
     */
    
    public void visit(IrClassDecl node) {
    	for (IrMemberDecl m : node.getMembers()) {
    		m.accept(this);
    	}
    	
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
    
    	current_type = null;
    }
    
    // note that the parser rejects any fields that are declared after
    // method declarations; hence we do not worry about method ids.
    @Override
    public void visit(IrBaseDecl node) {
    	IrIdentifier id_node = node.getId();
    	
    	String id = id_node.getId();
    	HashMap<String, Type> field_table = getCurrentEnv().getFieldTable();
    	HashMap<String, String> symbol_table = getCurrentEnv().getSymbolTable();
    	
    	if (field_table.containsKey(id)) {
    		error_flag = true;
    		int line = id_node.getLineNumber();
    		int column = id_node.getColumnNumber();
    		String message = "Duplicate base variable identifier: " + id;
    		System.out.println(errorPosMessage(line, column) + message);
    	} else {
    		// visitor will add id regardless of current_type's validity.
    		field_table.put(id, current_type);
    		String symbol = "g" + String.valueOf(global_counter);
    		symbol_table.put(id, symbol);
    		node.setSymbol(symbol);
    		global_counter++;
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
    	            String symbol = "a" + String.valueOf(array_counter);
    	            array_symbols.put(id, symbol);
    	            node.setSymbol(symbol);
    				array_counter++;
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
    	local_counter = 1; // locals in different procedures do not affect each other. we reuse symbols as such.
    	
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
    	
    	
    	// create the local scope.
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
                // params are treated as locals.
                // thus, these need to be allocated during codegen phase.
                String symbol = "v" + String.valueOf(local_counter);
                method_env.getSymbolTable().put(name, symbol);
    			local_counter++;
    		}
    	}
    	
    	env_stack.push(method_env); // new env for each method.
    	node.getBlock().accept(this); // check the actual code in the env.
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
    	
    	// is the id is declared twice in the same scope?
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
    		
    		// the local needs its own symbol during codegen.
            String symbol = "v" + String.valueOf(local_counter);
            getCurrentEnv().getSymbolTable().put(id, symbol);
            node.setSymbol(symbol);
            local_counter++;
    	}
    }
    
    @Override
    public void visit(IrBlock node) {
    	for (IrVarDecl d : node.getVarDecls()) {
    		d.accept(this);
    	}
    	for (IrStatement s : node.getStatements()) {
    		s.accept(this);
    	}
    }
    
    @Override
    public void visit(IrBlockStmt node) {
    	IrBlock block = node.getBlock();
    	// create a nested local scope.
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
    	Ir current_body;
    	boolean legal_stmt = false;
    	
    	while (env != null) {
    		current_body = env.getCurrentBody();
    		if ((current_body instanceof IrForStmt) ||
    			(current_body instanceof IrWhileStmt)) {
    			legal_stmt = true;
    			break;
    		}
    		env = env.getPreviousEnv();
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
    	Ir current_body;
    	boolean legal_stmt = false;
    	
    	while (env != null) {
    		current_body = env.getCurrentBody();
    		if ((current_body instanceof IrForStmt) ||
    			(current_body instanceof IrWhileStmt)) {
    			legal_stmt = true;
    			break;
    		}
    		env = env.getPreviousEnv();
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
    		error_flag = true;
    		int line = node.getLineNumber();
    		int column = node.getColumnNumber();
    		String message = "Return statement must be contained in the block of a method";
    		System.out.println(errorPosMessage(line, column) + message);
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
    	
    	// add the counter to the for-loop env.
    	for_env.getFieldTable().put(id.getId(), Type.INT);
    	
        String symbol = "v" + String.valueOf(local_counter);
        for_env.getSymbolTable().put(id.getId(), symbol);
        node.setCounterSymbol(symbol);
        
        // TODO: DEPRECATED set base pointer offset for for loop counter.
        node.setBpOffset(local_counter);
        
        local_counter++;
    	
    	env_stack.push(for_env); // enter the new env.		
    	block.accept(this);      // execute the block.
    	env_stack.pop();         // exit the env.
    }
    
    @Override
    public void visit(IrIfStmt node) {
    	IrExpression condition = node.getCondition();
    	condition.accept(this); // is if-condition well-formed?
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
    	
    	// method is defined; set the type of the return expr, if appropriate.
    	node.getExprType(this);
    	
    	IrMethodDecl called_method = method_table.get(method_name.getId());
    	ArrayList<IrParameterDecl> params = called_method.getParams();
    	
    	// check that the args are well-formed.
    	for (IrExpression arg : node.getArgs()) {
    		arg.accept(this);
    	}
    	
    	// check if the method signatures match.
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
    				String message = "Mismatching argument type";
    				System.out.println(errorPosMessage(line, column) + message);
    
    				break;
    			}
    		}
    	}
    	
    	// if the method call is part of an expression, check return type
    	Type return_type = determineType(called_method.getReturnType());
    	if (currently_evaluating_expr > 0 && return_type == Type.VOID) {
    		error_flag = true;
    		int line = node.getMethodName().getLineNumber();
    		int column = node.getMethodName().getColumnNumber();
    		String message = "Void methods cannot be used in int or boolean expressions";
    		System.out.println(errorPosMessage(line, column) + message);
    	}
    }
    
    @Override
    public void visit(IrCalloutStmt node) {
    	// check that the args are well-formed.
    	// mismatched signatures are found during run-time.
    	for (IrCalloutArg arg : node.getArgs()) {
    		arg.accept(this);
    	}
    }
    
    @Override
    public void visit(IrAssignStmt node) {		
    	IrLocation loc = node.getLeft();
    	IrExpression expr = node.getRight();
    	
    	// check that loc and expr are well-formed.
    	loc.accept(this);
    	currently_evaluating_expr++;
    	expr.accept(this);
    	currently_evaluating_expr--;
    	
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
    }
    
    @Override
    public void visit(IrPlusAssignStmt node) {
    	IrLocation loc = node.getLeft();
    	IrExpression expr = node.getRight();
    	
    	// check that loc and expr are well-formed.
    	loc.accept(this);
    	currently_evaluating_expr++;
    	expr.accept(this);
    	currently_evaluating_expr--;
    	
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
    }
    
    @Override
    public void visit(IrMinusAssignStmt node) {
    	IrLocation loc = node.getLeft();
    	IrExpression expr = node.getRight();
    	
    	// check that loc and expr are well-formed.
    	loc.accept(this);
    	currently_evaluating_expr++;
    	expr.accept(this);
    	currently_evaluating_expr--;
    	
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
            String symbol = lookupVarSymbol(id_node);
            node.setSymbol(symbol);
    	    
    		// TODO: DEPRECATED var is defined, set node's bp offset!
    		int offset = Integer.parseInt(symbol.substring(1));
    		node.setBpOffset(offset);
    	}
    }
    
    @Override
    public void visit(IrArrayLocation node) {
    	// check that index expr is well-formed.
    	node.getIndex().accept(this);
    	
    	IrIdentifier id_node = node.getId();
    	if (!arrayIsDefined(id_node.getId())) {
    		error_flag = true;
    		int line = id_node.getLineNumber();
    		int column = id_node.getColumnNumber();
    		String message = "Array identifier is undefined: " + id_node.getId();
    		System.out.println(errorPosMessage(line, column) + message);
    	}
    	
    	// pass the symbol rep.
    	String symbol = array_symbols.get(id_node.getId());
    	node.setSymbol(symbol);
    	
    	// pass the array size.
    	long size = lookupArraySize(id_node);
    	node.setArraySize(size);
    	
    	// array type checking.
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
    			} else if (lhs_type != rhs_type) {
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
    	expr.accept(this); // check that the expr is well-formed.
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
    
    // functionally equivalent to VarLocation.
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
            String symbol = lookupVarSymbol(node);
            node.setSymbol(symbol);
            
            // TODO: DEPRECATED var is defined, set node's bp offset!
            int offset = Integer.parseInt(symbol.substring(1));
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

    @Override
    public void visit(IrBoolLiteral node) {
        // TODO Auto-generated method stub
        // no need to do anything here.
    }

}