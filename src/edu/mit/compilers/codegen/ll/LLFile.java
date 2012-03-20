package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLFile implements LLNode {

    private ArrayList<LLGlobalDecl> global_decls;
    private ArrayList<LLArrayDecl> array_decls;
    private ArrayList<LLMethodDecl> methods;
    private ArrayList<LLStringLiteral> strings;
    private LLMethodDecl main;

    private LLLabel array_oob_label;
    private LLLabel missing_return_label;
    private LLLabel div_by_zero_label;
    
    public LLFile(LLMethodDecl mainMethod) {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
        main = mainMethod;
        
        array_oob_label = new LLLabel("ARRAY_OUT_OF_BOUNDS");
        missing_return_label = new LLLabel("MISSING_RETURN");
        div_by_zero_label = new LLLabel("DIVIDE_BY_ZERO");
    }
    
    public LLFile() {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
        
        array_oob_label = new LLLabel("ARRAY_OUT_OF_BOUNDS");
        missing_return_label = new LLLabel("MISSING_RETURN");
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
    
    public LLLabel getArrayOobLabel() {
		return array_oob_label;
	}

	public void setArrayOobLabel(LLLabel arrayOobLabel) {
		array_oob_label = arrayOobLabel;
	}

	public LLLabel getMissingReturnLabel() {
		return missing_return_label;
	}

	public void setMissingReturnLabel(LLLabel missingReturnLabel) {
		missing_return_label = missingReturnLabel;
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
