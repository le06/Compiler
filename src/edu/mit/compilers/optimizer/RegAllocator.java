package edu.mit.compilers.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;

public class RegAllocator implements LLNodeVisitor, Optimization {
	//private final String RAX = "%rax";
    //private final String RBP = "(%rbp)";    
    private final String RDI = "%rdi";
    private final String RSI = "%rsi";
    private final String RDX = "%rdx";
    private final String RCX = "%rcx";
    private final String R8  = "%r8";
    private final String R9  = "%r9";
    //private final String R10 = "%r10";
    //private final String R11 = "%r11";
    private final String R12 = "%r12";
    private final String R13 = "%r13";
    private final String R14 = "%r14";
    private final String R15 = "%r15";
    
    private final String[] registers = {RDI, RSI, RDX, RCX, R8, R9, R12, R13, R14, R15};
    private final int NUM_REGS = 10;
    
    HashMap<Integer, HashSet<String>> inMap;
	HashMap<Integer, HashSet<String>> outMap;
	HashMap<Integer, HashSet<String>> useMap;
	HashMap<Integer, HashSet<String>> defMap;
	
	HashMap<String, String> assignments;
	
	HashSet<String> currentSet;
	private MODE currentMode;
	
	LLMethodDecl thisMethod;
	
	private final boolean DEBUG = true;
	
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
		assignments = new HashMap<String, String>();
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
		HashMap<String, HashSet<String>> graphCopy = new HashMap<String, HashSet<String>>();
		
		// Get live ranges
		for (HashSet<String> in : inMap.values()) {
			for (String s1 : in) {
				for (String s2 : in) {
					if (!(s1.equals(s2))) {
						// Builds graph such that every node points to the 
						// set of its neighbors
						if (!graph.containsKey(s1)) {
							graph.put(s1, new HashSet<String>());
							graphCopy.put(s1, new HashSet<String>());
						}
						graph.get(s1).add(s2);
						graphCopy.get(s1).add(s2);
					}
				}
			}
		}
		
		
		
		HashSet<String> nodes = new HashSet<String>();
		for (String node : graph.keySet()) {
			nodes.add(node);
		}
		
		// Greedy alloc
		Stack<String> stack = new Stack<String>();
		Stack<String> spilled = new Stack<String>();
		
		ArrayList<String> removed = new ArrayList<String>();;
		
		while (nodes.size() > 0) {
			for (String var : nodes) {
				if (graph.get(var).size() < NUM_REGS) {
					stack.push(var);
					for (String neighbor : graph.get(var)) {
						graph.get(neighbor).remove(var);
					}
					removed.add(var);
				}
			}
			
			for (int i = 0; i < removed.size(); i++) {
				nodes.remove(removed.get(i));
				removed.remove(i);
			}
			
			// Spill a random var
			for (String s : nodes) {
				for (String neighbor : graph.get(s)) {
					graph.get(neighbor).remove(s);
				}
				removed.add(s);
				break;
			}
			
			for (int i = 0; i < removed.size(); i++) {
				nodes.remove(removed.get(i));
				removed.remove(i);
			}
		}
		
		HashMap<String, HashSet<String>> potentialRegs = new HashMap<String, HashSet<String>>();
		for (String s : graphCopy.keySet()) {
			HashSet<String> z = new HashSet<String>();
			for (String reg : registers) {
				z.add(reg);
			}
			potentialRegs.put(s,  z);
		}
		
		// Assign registers
		while (stack.size() > 0) {
			String current = stack.pop();
			String ass = null;
			for (String pot : potentialRegs.get(current)) {
				ass = pot;
				break;
			}
			
			assignments.put(current, ass);
			if (DEBUG) { System.out.println(current + ": " + ass); }
			thisMethod.addReg(ass);
			
			for (String neighbor : graphCopy.get(current)) {
				potentialRegs.get(neighbor).remove(ass);
			}
		}
		
		// Use the assignments to set the address of each var
		for (int i : allBlocks.keySet()) {
			assign(allBlocks.get(i));
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
	
	private void assign(BasicBlock b) {
		currentMode = MODE.REG_ALLOC;
		for (LLNode n : b.getInstructions()) {
			n.accept(this);
		}
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
		// Shouldn't occur
	}
	@Override
	public void visit(LLGlobalDecl node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(LLArrayDecl node) {
		// Do nothing
	}
	@Override
	public void visit(LLMalloc node) {
		// Shouldn't occur
	}
	@Override
	public void visit(LLMethodDecl node) {
		if (currentMode == MODE.DEF) {
			thisMethod = node;
		} else if (currentMode == MODE.REG_ALLOC) {
			for (LLVarLocation var : node.getArgs()) {
				if (assignments.containsKey(var.getLabel())) {
					var.putInRegister(assignments.get(var.getLabel()));
				}
			}
		}
		if (DEBUG) { System.out.println(node.getName()); }
	}
	@Override
	public void visit(LLEnvironment node) {
		// Shouldn't occur
	}
	
	@Override
	public void visit(LLAssign node) {
		if (node.getLoc() instanceof LLVarLocation) {
			LLVarLocation loc = ((LLVarLocation)node.getLoc());
			String dest = loc.getLabel();
			if (currentMode == MODE.DEF) {
				currentSet.add(dest);
			} else if (currentMode == MODE.REG_ALLOC) {
				if (assignments.containsKey(dest)) {
					loc.putInRegister(assignments.get(dest));
				}
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
		// Do nothing
	}
	@Override
	public void visit(LLVarLocation node) {
		if (currentMode == MODE.USE) {
			currentSet.add(node.getLabel());
		} else if (currentMode == MODE.REG_ALLOC) {
			if (assignments.containsKey(node.getLabel())) {
				node.putInRegister(assignments.get(node.getLabel()));
			}
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
		// Do nothing
	}
	@Override
	public void visit(LLIntLiteral node) {
		// Do nothing
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
		// Do nothing
	}

}
