package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.Writer;

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

public class CodeGenerator implements llNodeVisitor {
    
    private Writer outputStream;
    
    public void outputASM(Writer output, llFile decafFile) {
        outputStream = output;
        decafFile.accept(this);
    }
    
    private void writeText(String text) {
        try {
            outputStream.write(text);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(-1);
        }
    }
    
    private void writeLine(String text) {
        writeText("\t".concat(text).concat("\n"));
    }

    @Override
    public void visit(llArrayAccess node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llArrayDec node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llAssign node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llBinOp node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llBoolLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llCallout node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llEnvironment node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llExpression node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llFile node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llFunctionCall node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llGlobalDec node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llJump node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llLabel node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llMethodCall node) {
        // Assumes all parameters are in their proper locations
        writeLine("call " + node.getMethodName());
        writeLine("push %rax");
    }

    @Override
    public void visit(llNode node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llPop node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llPush node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llReturn node) {
        // TODO Auto-generated method stub
        
        if (node.hasReturn()) {
            // mov %rsp, rax
        }
        
        // Write ret
    }

    @Override
    public void visit(llStringLiteral node) {
        writeText(node.getLabelASM() + "\n\t.string " +
                  node.getText() + "\n");
    }

    @Override
    public void visit(llUnaryNeg node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llUnaryNot node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llVarAccess node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(llVarDec node) {
        // TODO Auto-generated method stub
        
    }

}
