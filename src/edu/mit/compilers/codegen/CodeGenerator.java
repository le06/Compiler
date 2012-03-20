package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.Writer;

import edu.mit.compilers.codegen.ll.LLMalloc;
import edu.mit.compilers.codegen.ll.LLArrayLocation;
import edu.mit.compilers.codegen.ll.LLArrayDecl;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLBoolLiteral;
import edu.mit.compilers.codegen.ll.LLMov;
import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNodeVisitor;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLReturn;
import edu.mit.compilers.codegen.ll.LLStringLiteral;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLUnaryNot;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class CodeGenerator implements LLNodeVisitor {
	
    private Writer outputStream;
    
    public void outputASM(Writer output, LLFile decafFile) {
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

    public CodeGenerator() {
    	
    }
    
    @Override
    public void visit(LLFile node) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void visit(LLArrayLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLArrayDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLAssign node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLBinaryOp node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLCallout node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LLEnvironment node) {
        // TODO Auto-gegetName()nerated method stub
        
    }

    @Override
    public void visit(LLExpression node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLGlobalDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLJump node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLLabel node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLMethodCall node) {
        // Assumes all parameters are in their proper locations
        writeLine("call " + node.getMethodName());
        writeLine("push %rax");
    }

    @Override
    public void visit(LLReturn node) {
        // TODO Auto-generated method stub
        
        if (node.hasReturn()) {
            // mov %rsp, rax
        }
        
        // Write ret
    }

    @Override
    public void visit(LLStringLiteral node) {
        writeText(node.getLabelASM() + "\n\t.string " +
                  node.getText() + "\n");
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLUnaryNot node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLVarLocation node) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void visit(LLMethodDecl llMethodDecl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMalloc llMalloc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMov llMov) {
		// TODO Auto-generated method stub
		
	}

}
