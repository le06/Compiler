package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLFile implements LLNode {

    private ArrayList<LLGlobalDecl> global_decls;
    private ArrayList<LLArrayDecl> array_decls;
    private ArrayList<LLMethodDecl> methods;
    private ArrayList<LLStringLiteral> strings;
    private LLMethodDecl main;

    private LLCallout array_oob_callout;
    private LLCallout missing_return_callout;
    private LLCallout div_by_zero_callout;
    
    private LLLabel array_oob_label;
    private LLLabel missing_return_label;
    private LLLabel div_by_zero_label;
    
    public LLFile(LLMethodDecl mainMethod) {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
        main = mainMethod;
        
        initErrorLabels();
    }
    
    public LLFile() {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
        
        initErrorLabels();
    }
    
    private void initErrorLabels() {
        String message = "\"Exception: array index out-of-bounds\\n\"";
        array_oob_callout = new LLCallout("\"printf\"");
        array_oob_callout.addParam(new LLStringLiteral(message, new LLLabel("error")));
        array_oob_label = new LLLabel("ARRAY_OUT_OF_BOUNDS");
        
        message = "\"Exception: non-void function missing return statement\\n\"";
        missing_return_callout = new LLCallout("\"printf\"");
        missing_return_callout.addParam(new LLStringLiteral(message, new LLLabel("error")));
        missing_return_label = new LLLabel("MISSING_RETURN");
        
        message = "\"Exception: division by zero\\n\"";
        div_by_zero_callout = new LLCallout("\"printf\"");
        div_by_zero_callout.addParam(new LLStringLiteral(message, new LLLabel("error")));
        div_by_zero_label = new LLLabel("DIVIDE_BY_ZERO");
    }
    
    public void addGlobalDec(LLGlobalDecl decl) {
        global_decls.add(decl);
    }   
    
    public void addArrayDec(LLArrayDecl decl) {
        array_decls.add(decl);
    }
    
    public void addMethod(LLMethodDecl method) {
        methods.add(method);
    }
    
    // error 1: array bounds.
    public LLCallout getArrayOobCallout() {
    	return array_oob_callout;
    }
    
    public LLLabel getArrayOobLabel() {
		return array_oob_label;
	}

	public void setArrayOobLabel(LLLabel arrayOobLabel) {
		array_oob_label = arrayOobLabel;
	}

	// error 2: missing return stmt.
    public LLCallout getMissingReturnCallout() {
    	return missing_return_callout;
    }
	
	public LLLabel getMissingReturnLabel() {
		return missing_return_label;
	}

	public void setMissingReturnLabel(LLLabel missingReturnLabel) {
		missing_return_label = missingReturnLabel;
	}

	// error 3: div-by-zero.
    public LLCallout getDivByZeroCallout() {
    	return div_by_zero_callout;
    }
	
	public LLLabel getDivByZeroLabel() {
		return div_by_zero_label;
	}

	public void setDivByZeroLabel(LLLabel divByZeroLabel) {
		div_by_zero_label = divByZeroLabel;
	}
	
	public void setMain(LLMethodDecl main_method) {
        main = main_method;
    }
    
    public void addString(LLStringLiteral str) {
        strings.add(str);
    }

    
    public ArrayList<LLGlobalDecl> getGlobalDecls() {
		return global_decls;
	}

	public ArrayList<LLArrayDecl> getArrayDecls() {
		return array_decls;
	}

	public ArrayList<LLMethodDecl> getMethods() {
		return methods;
	}

	public LLMethodDecl getMain() {
		return main;
	}
    
	public ArrayList<LLStringLiteral> getStringLiterals() {
		return strings;
	}
	
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
