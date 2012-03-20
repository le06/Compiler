package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.ll.llArrayAccess;
import edu.mit.compilers.codegen.ll.llArrayDec;
import edu.mit.compilers.codegen.ll.llAssign;
import edu.mit.compilers.codegen.ll.llBinOp;
import edu.mit.compilers.codegen.ll.llBoolLiteral;
import edu.mit.compilers.codegen.ll.llCallout;
import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llFile;
import edu.mit.compilers.codegen.ll.llFunctionCall;
import edu.mit.compilers.codegen.ll.llGlobalDec;
import edu.mit.compilers.codegen.ll.llIntLiteral;
import edu.mit.compilers.codegen.ll.llJump;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llMethodCall;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llNodeVisitor;
import edu.mit.compilers.codegen.ll.llPop;
import edu.mit.compilers.codegen.ll.llPush;
import edu.mit.compilers.codegen.ll.llReturn;
import edu.mit.compilers.codegen.ll.llStringLiteral;
import edu.mit.compilers.codegen.ll.llUnaryNeg;
import edu.mit.compilers.codegen.ll.llUnaryNot;
import edu.mit.compilers.codegen.ll.llVarAccess;
import edu.mit.compilers.codegen.ll.llVarDec;
import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class LabelNamer implements llNodeVisitor {
    private HashMap<String, Integer> table;
    private llFile currentFile;
    private static String DEFAULT_LABEL = "l";
    
    // Makes all label names unique and 
    public void name(llFile root) {
        currentFile = root;
        table = new HashMap<String, Integer>();
        table.put(DEFAULT_LABEL, 0);
        currentFile.accept(this);
    }

    @Override
    public void visit(llArrayAccess node) {
        // Do nothing
    }

    @Override
    public void visit(llArrayDec node) {
     // Do nothing
    }

    @Override
    public void visit(llAssign node) {
     // Do nothing
    }

    @Override
    public void visit(llBinOp node) {
     // Do nothing
    }

    @Override
    public void visit(llBoolLiteral node) {
     // Do nothing
    }

    @Override
    public void visit(llCallout node) {
     // Do nothing
    }

    @Override
    public void visit(llEnvironment node) {
     // Do nothing
    }

    @Override
    public void visit(llExpression node) {
     // Do nothing
    }

    @Override
    public void visit(llFile node) {
     // Do nothing
    }

    @Override
    public void visit(llFunctionCall node) {
     // Do nothing
    }

    @Override
    public void visit(llGlobalDec node) {
     // Do nothing
    }

    @Override
    public void visit(llIntLiteral node) {
     // Do nothing
    }

    @Override
    public void visit(llJump node) {
     // Do nothing
    }

    @Override
    public void visit(llLabel node) {
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
    public void visit(llMethodCall node) {
     // Do nothing
    }

    @Override
    public void visit(llNode node) {
     // Do nothing
    }

    @Override
    public void visit(llPop node) {
     // Do nothing
    }

    @Override
    public void visit(llPush node) {
     // Do nothing
    }

    @Override
    public void visit(llReturn node) {
     // Do nothing
    }

    @Override
    public void visit(llStringLiteral node) {
        currentFile.addString(node);
    }

    @Override
    public void visit(llUnaryNeg node) {
     // Do nothing 
    }

    @Override
    public void visit(llUnaryNot node) {
     // Do nothing
    }

    @Override
    public void visit(llVarAccess node) {
     // Do nothing
    }

    @Override
    public void visit(llVarDec node) {
     // Do nothing
    }

}
