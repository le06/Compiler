package edu.mit.compilers.codegen;

import java.util.ArrayList;
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
        for (LLGlobalDecl g : node.getGlobalDecls()) {
            g.accept(this);
        }
        for (LLArrayDecl a : node.getArrayDecls()) {
            a.accept(this);
        }
        for (LLMethodDecl m : node.getMethods()) {
            m.accept(this);
        }
        
        node.getMain().accept(this);
        
        // check the string literals associated with the run-time error callouts.
        node.getArrayOobCallout().accept(this);
        node.getMissingReturnCallout().accept(this);
        node.getDivByZeroCallout().accept(this);
        
        // make sure the run-time error labels aren't duplicated.
        node.getArrayOobLabel().accept(this);
        node.getMissingReturnLabel().accept(this);
        node.getDivByZeroLabel().accept(this);
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
    public void visit(LLMalloc llMalloc) {
        // Do Nothing
    }
    
    @Override
    public void visit(LLMethodDecl llMethodDecl) {
        llMethodDecl.getEnv().accept(this);
    }
    
    @Override
    public void visit(LLEnvironment node) {
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }
    }
    
    @Override
    public void visit(LLAssign node) {
        node.getExpr().accept(this);
    }
    
    @Override
    public void visit(LLMethodCall node) {
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }
    }
    
    @Override
    public void visit(LLCallout node) {
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }
    }
    
    @Override
    public void visit(LLStringLiteral node) {
        currentFile.addString(node);
        node.getLabel().accept(this);
    }
    
    @Override
    public void visit(LLVarLocation node) {
        // Do Nothing
    }
    
    @Override
    public void visit(LLArrayLocation node) {
        // Do Nothing
    }

    @Override
    public void visit(LLBinaryOp node) {
    	LLLabel label = node.getLabel();
    	if (label != null) { // only AND/OR ops have labels.
    		node.getLabel().accept(this);
    	}
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // Do Nothing
    }

    @Override
    public void visit(LLUnaryNot node) {
        node.getExpr().accept(this);
    }
    
    @Override
    public void visit(LLBoolLiteral node) {
        // Do Nothing
    }

    @Override
    public void visit(LLIntLiteral node) {
        // Do Nothing
    }

    @Override
    public void visit(LLJump node) {
        // label can be ignored since it will be walked anyway.
    	// do check the expr.
    	LLExpression expr = node.getCond();
    	if (expr != null) {
    		expr.accept(this);
    	}
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
    public void visit(LLReturn node) {
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

    @Override
    public void visit(LLCmp node) {
        // TODO Auto-generated method stub
        
    }

}
