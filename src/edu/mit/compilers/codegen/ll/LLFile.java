package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLFile implements LLNode {
	
    ArrayList<LLGlobalDecl> global_decls;
    ArrayList<LLArrayDecl> array_decls;
    ArrayList<LLMethodDecl> methods;
    ArrayList<LLStringLiteral> strings;
    LLMethodDecl main;
    LLMethodDecl array_oob;			// TODO: implement these!
    LLMethodDecl missing_return;	//
    
    public LLFile(LLMethodDecl mainMethod) {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
        main = mainMethod;
    }
    
    public LLFile() {
        global_decls = new ArrayList<LLGlobalDecl>();
        array_decls = new ArrayList<LLArrayDecl>();
        methods = new ArrayList<LLMethodDecl>();
        strings = new ArrayList<LLStringLiteral>();
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
    
    public void addString(LLStringLiteral str) {
        strings.add(str);
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
