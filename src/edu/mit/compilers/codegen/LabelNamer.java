package edu.mit.compilers.codegen;

import java.util.HashMap;

import edu.mit.compilers.codegen.ll.*;


public class LabelNamer implements LLNodeVisitor {
    private HashMap<String, Integer> table;
    private LLFile currentFile;
    private static String DEFAULT_LABEL = "l";
    
    // Makes all label names unique and 
    public void name(LLFile root) {
        currentFile = root;
        table = new HashMap<String, Integer>();
        table.put(DEFAULT_LABEL, 0);
        currentFile.accept(this);
    }

    @Override
    public void visit(LLFile node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLGlobalDecl node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLArrayDecl node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLArrayLocation node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLAssign node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLBinaryOp node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLCallout node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLEnvironment node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLIntLiteral node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLJump node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLLabel node) {
        int suffix;
        String newName;
        if (node.getName() == null) {
            throw new  RuntimeException("Label must have base name");
        } else if (table.containsKey(node.getName())) {
            suffix = table.get(node.getName()) + 1;
            newName = node.getName().concat("_") + suffix;
            
            while (table.containsKey(newName)) {
                suffix++;
                newName = node.getName().concat("_") + suffix;
            }
            
            table.put(node.getName(), suffix);
            node.setName(newName);
        } 
        
        table.put(node.getName(), 0);
    }

    @Override
    public void visit(LLMethodCall node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLReturn node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLStringLiteral node) {
        currentFile.addString(node);
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLUnaryNot node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLVarLocation node) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLMethodDecl llMethodDecl) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLMalloc llMalloc) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLMov llMov) {
        // Do Nothing
        
    }

    @Override
    public void visit(LLNop node) {
        // Do Nothing
        
    }

}
