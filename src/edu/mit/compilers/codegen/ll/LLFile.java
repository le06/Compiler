package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLFile implements LLNode {

	private ArrayList<LLGlobalDecl> global_decls;
    private ArrayList<LLArrayDecl> array_decls;
    private ArrayList<LLMethodDecl> methods;
    private LLMethodDecl main;
    
    public LLFile(LLMethodDecl mainMethod) {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        main = mainMethod;
    }
    
    public LLFile() {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
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
    
    public void setMain(LLMethodDecl main_method) {
        main = main_method;
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
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
