package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.codegen.ll.LLArrayDecl;
import edu.mit.compilers.codegen.ll.LLArrayLocation;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLBoolLiteral;
import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMalloc;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLMov;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNodeVisitor;
import edu.mit.compilers.codegen.ll.LLNop;
import edu.mit.compilers.codegen.ll.LLReturn;
import edu.mit.compilers.codegen.ll.LLStringLiteral;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLUnaryNot;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class AddressAssigner implements LLNodeVisitor {
    private final int OFFSET = 8;
    private final String RBP = "(%rbp)";
    private int currentOffset = OFFSET;
    
    private HashMap<String, Integer> local_counts;
    
    private String getNextAddress() {
        String output = "-" + currentOffset + RBP;
        currentOffset += OFFSET;
        return output;
    }
    
    // offset = num of quadwords from base pointer: 1,2,3...
    private String generateAddress(int offset) {
    	String inst_offset = String.valueOf(offset * OFFSET);
    	String output = "-" + inst_offset + RBP;
    	return output;
    }
    
    private void resetAddress() {
        currentOffset = OFFSET;
    }
    

	public void setLocalCounts(HashMap<String, Integer> localCounts) {
		local_counts = localCounts;
	}
    
    public void assign(LLFile file) {
        file.accept(this);
    }

    @Override
    public void visit(LLFile node) {
        resetAddress();
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
    }

    // maintain a table of labels for global identifiers.
    
    
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
        resetAddress();

        String id = node.getName();
        int num_locals = local_counts.get(id);
        currentOffset += (num_locals * OFFSET);
        //System.out.println(id + ", " + num_locals);
        node.getEnv().accept(this);
        
        int num_temps = currentOffset / OFFSET;
        node.setNumTemps(num_temps);
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
        node.getLoc().accept(this);
    }

    @Override
    public void visit(LLMethodCall node) {
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLCallout node) {
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLStringLiteral node) {
        // do nothing. never evaluated as part of expr.
    }

    @Override
    public void visit(LLVarLocation node) {
        int offset = node.getBpOffset();
        
        if (offset > 0) { // local
        	String address = generateAddress(offset);
        	node.setLocation(address);
        } else { // global
        	String address = "." + node.getLabel();
        	node.setLocation(address);
        }
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLArrayLocation node) {
    	LLExpression index = node.getIndexExpr();
    	index.accept(this);
    	
    	String location = "." + node.getLabel();
    	node.setLocation(location);
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLBinaryOp node) {
    	LLExpression left = node.getLhs();
    	LLExpression right = node.getRhs();
    	left.accept(this);
    	right.accept(this);
        node.setAddress(getNextAddress());        
    }

    @Override
    public void visit(LLUnaryNeg node) {
    	node.getExpr().accept(this);
        node.setAddress(getNextAddress());
    }

    @Override
    public void visit(LLUnaryNot node) {
    	node.getExpr().accept(this);
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
    	// jump nodes can contain exprs. need to assign address if so!
    	LLExpression cond = node.getCond();
    	if (cond != null) {
    		cond.accept(this);
    	}
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
    	LLExpression expr = node.getExpr();
    	if (expr != null) {
    		expr.accept(this);
    	}
    }

    @Override
    public void visit(LLNop node) {
        // TODO Auto-generated method stub
        
    }

}
