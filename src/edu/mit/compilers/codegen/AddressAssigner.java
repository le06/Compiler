package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.ll.LLArrayDecl;
import edu.mit.compilers.codegen.ll.LLArrayLocation;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLBoolLiteral;
import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMalloc;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLMov;
import edu.mit.compilers.codegen.ll.LLNodeVisitor;
import edu.mit.compilers.codegen.ll.LLReturn;
import edu.mit.compilers.codegen.ll.LLStringLiteral;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLUnaryNot;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class AddressAssigner implements LLNodeVisitor {
    private final int OFFSET = 8;
    private final String RBP = "(%rbp)";
    private int currentOffset = OFFSET;
    
    private LLMethodDecl currentMethod;
    
    private String getNextAddress() {
        String out = "-" + currentOffset + RBP;
        currentOffset += OFFSET;
        return out;
    }
    
    private void resetAddress() {
        currentOffset = OFFSET;
    }
    
    public void assign(LLFile file) {
        file.accept(this);
    }

    @Override
    public void visit(LLFile node) {
        resetAddress();
    }

    @Override
    public void visit(LLGlobalDecl node) {
        // Do nothing
    }

    @Override
    public void visit(LLArrayDecl node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLMalloc node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLMethodDecl node) {
        if (currentMethod != null) {
            currentMethod.setNumTemps(currentOffset / OFFSET);
        }
        resetAddress();
    }

    @Override
    public void visit(LLEnvironment node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLAssign node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLMethodCall node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLCallout node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLStringLiteral node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLVarLocation node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLArrayLocation node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLBinaryOp node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLUnaryNeg node) {
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLUnaryNot node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLBoolLiteral node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLIntLiteral node) {
        node.setAddress(getNextAddress());
        
    }

    @Override
    public void visit(LLJump node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLLabel node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLMov node) {
     // Do nothing
        
    }

    @Override
    public void visit(LLReturn node) {
     // Do nothing
        
    }

}
