package edu.mit.compilers.optimizer;

import java.util.HashMap;
import java.util.HashSet;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;

public class RegAllocator implements LLNodeVisitor, Optimization {
	private final String RAX = "%rax";
    private final String RBP = "(%rbp)";    
    private final String RDI = "%rdi";
    private final String RSI = "%rsi";
    private final String RDX = "%rdx";
    private final String RCX = "%rcx";
    private final String R8  = "%r8";
    private final String R9  = "%r9";
    private final String R10 = "%r10";
    private final String R11 = "%r11";
    
    HashMap<Integer, HashSet<String>> inMap;
	HashMap<Integer, HashSet<String>> outMap;
	HashMap<Integer, HashSet<String>> useMap;
	HashMap<Integer, HashSet<String>> defMap;
	
	HashSet<String> currentSet;
	private MODE currentMode;
	
	private enum MODE {
		DEF,
		USE,
		REG_ALLOC;
	}
	
	public RegAllocator() {
		inMap = new HashMap<Integer, HashSet<String>>();
		outMap = new HashMap<Integer, HashSet<String>>();
		defMap = new HashMap<Integer, HashSet<String>>();
		useMap = new HashMap<Integer, HashSet<String>>();
	}
    
	@Override
	public void optimize(BasicBlock method) {
		// First, get live ranges:
		HashMap<Integer, BasicBlock> allBlocks = getBlocksInMethod(method);
		
		// Define gen/kill
		
		for (int i : allBlocks.keySet()) {
			inMap.put(i, new HashSet<String>());
			outMap.put(i, new HashSet<String>());
			useMap.put(i, getUse(allBlocks.get(i)));
			defMap.put(i, getDef(allBlocks.get(i)));
		}
		boolean repeat;
		
		do {
			repeat = false;
			for (int i : allBlocks.keySet()) {
				HashSet<String> in = inMap.get(i);
				HashSet<String> out = outMap.get(i);
				HashSet<String> inPrime = (HashSet<String>)in.clone();
				HashSet<String> outPrime = (HashSet<String>)out.clone();
				
				//in[n] = use[n] U (out[n] – def[n])
				in = new HashSet<String>();
				in.addAll(useMap.get(i)); // in = use
				for (String def : defMap.get(i)) { // out = out - def
					out.remove(def);
				}
				in.addAll(out); //in = in U out | in = use, out = out - def
				inMap.put(i, in);
				
				
				// out[n] = U_{s \in succ[n]) in[s]
				out = new HashSet<String>();
				for (BasicBlock child : allBlocks.get(i).getChildren()) {
					out.addAll(inMap.get(child.getNum()));
				}
				outMap.put(i, out);
				
				if (!repeat) {
					repeat = (!inPrime.equals(in) || !outPrime.equals(out));
				}
			}
		} while (repeat);
		
		HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
		
		// Get live ranges
		for (HashSet<String> in : inMap.values()) {
			for (String s1 : in) {
				for (String s2 : in) {
					if (!(s1.equals(s2))) {
						// Builds graph such that every node points to the 
						// set of its neighbors
						if (!graph.containsKey(s1)) {
							graph.put(s1, new HashSet<String>());
						}
						graph.get(s1).add(s2);
					}
				}
			}
		}
	}
	
	private HashMap<Integer, BasicBlock> getBlocksInMethod(BasicBlock b) {
		HashMap<Integer, BasicBlock> out = new HashMap<Integer, BasicBlock>();
		getBlocksInMethod(b, out);
		return out;
	}
	
	private void getBlocksInMethod(BasicBlock b, HashMap<Integer, BasicBlock> out) {
		out.put(b.getNum(), b);
		for (BasicBlock c : b.getChildren()) {
			if (!out.containsKey(c.getNum())) {
				out.put(c.getNum(), c);
				getBlocksInMethod(c, out);
			}
		}
	}
	
	private HashSet<String> getDef(BasicBlock b) {
		HashSet<String> defs = new HashSet<String>();
		currentSet = defs;
		currentMode = MODE.DEF;
		for (LLNode n : b.getInstructions()) {
			n.accept(this);
		}
		
		return defs;
	}
	
	private HashSet<String> getUse(BasicBlock b) {
		HashSet<String> uses = new HashSet<String>();
		currentSet = uses;
		currentMode = MODE.USE;
		for (LLNode n : b.getInstructions()) {
			n.accept(this);
		}
		
		return uses;
	}
    
	@Override
	public void visit(LLFile node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLGlobalDecl node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLArrayDecl node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLMalloc node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLMethodDecl node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLEnvironment node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLAssign node) {
		if (node.getLoc() instanceof LLVarLocation) {
			if (currentMode == MODE.DEF) {
				String dest = ((LLVarLocation)node.getLoc()).getLabel();
				currentSet.add(dest);
			} 
		} else {
			node.getLoc().accept(this);
		}
		node.getExpr().accept(this);
	}
	@Override
	public void visit(LLMethodCall node) {
		for (LLExpression p : node.getParams()) {
			p.accept(this);
		}
	}
	@Override
	public void visit(LLCallout node) {
		for (LLExpression p : node.getParams()) {
			p.accept(this);
		}
	}
	@Override
	public void visit(LLStringLiteral node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLVarLocation node) {
		if (currentMode == MODE.USE) {
			currentSet.add(node.getLabel());
		}
	}
	@Override
	public void visit(LLArrayLocation node) {
		node.getIndexExpr().accept(this);
	}
	@Override
	public void visit(LLBinaryOp node) {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
	}
	@Override
	public void visit(LLUnaryNeg node) {
		node.getExpr().accept(this);
	}
	@Override
	public void visit(LLUnaryNot node) {
		node.getExpr().accept(this);
	}
	@Override
	public void visit(LLBoolLiteral node) {
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
	public void visit(LLMov node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLCmp node) {
		node.getL().accept(this);
		node.getR().accept(this);
	}
	@Override
	public void visit(LLReturn node) {
		if (node.hasReturn()) {
			node.getExpr().accept(this);
		}
	}
	@Override
	public void visit(LLNop node) {
		// TODO Auto-generated method stub
		
	}

}
