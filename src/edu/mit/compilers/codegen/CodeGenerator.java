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
    
    /*
     * Printing functions.
     */
    
    private void writeText(String text) {
        try {
            outputStream.write(text);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(-1);
        }
    }
    
    private void writeLine(String text) {
    	for (int i = 0; i < tab_level; i++) {
    		writeText("\t");
    	}
    	writeText(text);
    	writeText("\n");
    }
    
    private Writer outputStream;
    private int tab_level;
    private LLNode root;
    
    public CodeGenerator(LLNode root) {
    	tab_level = 0;
    }
    
    // entry point for LL tree walker.
    public void outputASM(Writer output, LLFile decafFile) {
        outputStream = output;
        decafFile.accept(this);
    }
    
    /*
     * Low-level node visitor methods.
     * These are responsible for generating the actual ASM.
     */
    
    @Override
    public void visit(LLFile node) {
    	// check these in order...
        for (LLGlobalDecl g : node.getGlobalDecls()) {
        	g.accept(this);
        }
        for (LLArrayDecl a : node.getArrayDecls()) {
        	a.accept(this);
        }
        for (LLMethodDecl m : node.getMethods()) {
        	m.accept(this);
        }
        // finally the main method.
        node.getMain().accept(this);
        
    	// TODO: array oob function
    	// TODO: missing return statement function
    }
    
    @Override
    public void visit(LLGlobalDecl node) {
        node.getLabel().accept(this);;
        
        tab_level++;
        node.getMalloc().accept(this);
        tab_level--;
    }
    
    @Override
    public void visit(LLArrayDecl node) {
        // TODO Auto-generated method stub
        
    }    
    
	@Override
	public void visit(LLMalloc node) {
		// TODO Auto-generated method stub
		
	}
    
///////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(LLMethodDecl node) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void visit(LLEnvironment node) {
        // TODO Auto-gegetName()nerated method stub
        
    }    
    
///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLAssign node) {
        // TODO Auto-generated method stub
        
    }    
    
    @Override
    public void visit(LLMethodCall node) {
        // Assumes all parameters are in their proper locations
        writeLine("call " + node.getMethodName());
        writeLine("push %rax");
    }
    
    @Override
    public void visit(LLCallout node) {
        // TODO Auto-generated method stub

    }    
    
    @Override
    public void visit(LLStringLiteral node) {
        writeText(node.getLabelASM() + "\n\t.string " +
                  node.getText() + "\n");
    }

///////////////////////////////////////////////////////////////////////////////    
    
    @Override
    public void visit(LLVarLocation node) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void visit(LLArrayLocation node) {
        // TODO Auto-generated method stub
        
    }

///////////////////////////////////////////////////////////////////////////////

    @Override
    public void visit(LLBinaryOp node) {
        // TODO Auto-generated method stub
        
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
    public void visit(LLBoolLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLJump node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLLabel node) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void visit(LLMov node) {
		// TODO Auto-generated method stub
		
	}    
    
    @Override
    public void visit(LLReturn node) {
        // TODO Auto-generated method stub
        
        if (node.hasReturn()) {
            // mov %rsp, rax
        }
        
        // Write ret
    }

}
