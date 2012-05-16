package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll2.LlArrayDecl;
import edu.mit.compilers.codegen.ll2.LlGlobalDecl;
import edu.mit.compilers.codegen.ll2.LlMethodDecl;
import edu.mit.compilers.codegen.ll2.LlStringLiteral;

public class LlProgram implements LlNode {

    private ArrayList<LlGlobalDecl> global_decls;
    private ArrayList<LlArrayDecl> array_decls;
    private ArrayList<LlMethodDecl> methods;
    private ArrayList<LlStringLiteral> strings;
    private LlMethodDecl main;
    
    public LlProgram() {
        global_decls = new ArrayList<LlGlobalDecl>();
        array_decls = new ArrayList<LlArrayDecl>();
        methods = new ArrayList<LlMethodDecl>();
        strings = new ArrayList<LlStringLiteral>();
    }
    
    public void addGlobalDecl(LlGlobalDecl decl) {
        global_decls.add(decl);
    }   
    
    public void addArrayDecl(LlArrayDecl decl) {
        array_decls.add(decl);
    }
    
    public void addMethod(LlMethodDecl method) {
        methods.add(method);
    }
    
    public void addString(LlStringLiteral str) {
        strings.add(str);
    }
    
    public void setMain(LlMethodDecl main) {
        this.main = main;
    }
    
    public ArrayList<LlGlobalDecl> getGlobalDecls() {
        return global_decls;
    }

    public ArrayList<LlArrayDecl> getArrayDecls() {
        return array_decls;
    }

    public ArrayList<LlMethodDecl> getMethods() {
        return methods;
    }

    public LlMethodDecl getMain() {
        return main;
    }
    
    public ArrayList<LlStringLiteral> getStringLiterals() {
        return strings;
    }

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub
        
    }
    
}
