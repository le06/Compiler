package edu.mit.compilers.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;

public class RegAlloc implements LLNodeVisitor {
	//private final String RAX = "%rax";
    //private final String RBP = "(%rbp)";    
    private final String RDI = "%rdi";
    private final String RSI = "%rsi";
    //private final String RDX = "%rdx";
    private final String RCX = "%rcx";
    private final String R8  = "%r8";
    private final String R9  = "%r9";
    //private final String R10 = "%r10";
    //private final String R11 = "%r11";
    private final String R12 = "%r12";
    private final String R13 = "%r13";
    private final String R14 = "%r14";
    private final String R15 = "%r15";
    
    private final String[] registers = {RDI, RSI, RCX, R8, R9, R12, R13, R14, R15};
    private final int NUM_REGS = 9;
	
	ArrayList<HashSet<String>> inMap;
	ArrayList<HashSet<String>> outMap;
	ArrayList<HashSet<String>> defMap;
	ArrayList<HashSet<String>> useMap;
	
	ArrayList<HashSet<Integer>> succ;
	
	HashMap<String, String> assignments;
	
	HashSet<String> currentSet;
	
	LLMethodDecl thisMethod;
	
	private enum MODE {
		ANALYZE,
		DEADCODE,
		REG_ALLOC;
	}
	final boolean DEBUG = false;
	
	MODE currentMode;
	int currentInstruction;
	
	HashMap<String, Integer> labelMap;
	
	public void allocMethod(ArrayList<LLNode> instructions, ArrayList<String> glbls) {
		inMap = new ArrayList<HashSet<String>>(instructions.size());
		outMap = new ArrayList<HashSet<String>>(instructions.size());
		defMap = new ArrayList<HashSet<String>>(instructions.size());
		useMap = new ArrayList<HashSet<String>>(instructions.size());
		succ = new ArrayList<HashSet<Integer>>(instructions.size());
		assignments = new HashMap<String, String>();
		
		HashSet<String> globals = new HashSet<String>(glbls);
		
		// Find successors of each instruction
		labelMap = new HashMap<String, Integer>();
		
		// First build label map
		for (int i = 0; i < instructions.size(); i++) {
			LLNode instr = instructions.get(i);
			if (instr instanceof LLLabel) {
				labelMap.put(((LLLabel)instr).getName(), i);
			}
		}
		
		// Then build succ
		for (int i = 0; i < instructions.size()-1; i++) {
			succ.add(i, new HashSet<Integer>());
			LLNode instr = instructions.get(i);
			if (instr instanceof LLJump) {
				LLJump jmp = (LLJump)instr;
				int target = labelMap.get(jmp.getLabel().getName());
				succ.get(i).add(target);
			}
			succ.get(i).add(i+1);
		}
		
		// If last instr jump:
		int lastI = instructions.size()-1;
		if (lastI >= 0) {
			LLNode instr = instructions.get(lastI);
			succ.add(lastI, new HashSet<Integer>());
			if (instr instanceof LLJump) {
				LLJump jmp = (LLJump)instr;
				succ.get(lastI).add(labelMap.get(jmp.getLabel()));
			}
		}
		
		// Define def/use blocks
		currentMode = MODE.ANALYZE;
		for (currentInstruction = 0; currentInstruction < instructions.size(); currentInstruction++) {
			defMap.add(currentInstruction, new HashSet<String>());
			useMap.add(currentInstruction, new HashSet<String>());
			inMap.add(currentInstruction, new HashSet<String>());
			outMap.add(currentInstruction, new HashSet<String>());
			instructions.get(currentInstruction).accept(this);
		}
		
		boolean repeat;
		do {
			repeat = false;
			for (currentInstruction = instructions.size()-1; currentInstruction >= 0; currentInstruction--) {
				HashSet<String> in = inMap.get(currentInstruction);
				HashSet<String> out = outMap.get(currentInstruction);
				HashSet<String> inPrime = (HashSet<String>)in.clone();
				HashSet<String> outPrime = (HashSet<String>)out.clone();
				
				//in[n] = use[n] U (out[n] – def[n])
				in = new HashSet<String>();
				in.addAll(useMap.get(currentInstruction)); // in = use
				for (String def : defMap.get(currentInstruction)) { // out = out - def
					out.remove(def);
				}
				in.addAll(out); //in = in U out | in = use, out = out - def
				//inMap.add(currentInstruction, in);
				inMap.set(currentInstruction, in);
				
				// out[n] = U_{s \in succ[n]) in[s]
				out = new HashSet<String>();
				for (int i : succ.get(currentInstruction)) {
					out.addAll(inMap.get(i));
				}
				//outMap.add(currentInstruction, out);
				outMap.set(currentInstruction, out);
				
				if (!repeat) {
					repeat = (!inPrime.equals(in) || !outPrime.equals(out));
				}
			}
		} while (repeat);
		
		
		// Eliminate dead code.
		currentMode = MODE.DEADCODE;
        for (currentInstruction = 0; currentInstruction < instructions.size(); currentInstruction++) {
            instructions.get(currentInstruction).accept(this);
        }
		
		HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> graphCopy = new HashMap<String, HashSet<String>>();
		
		// Get live ranges
		//for (HashSet<String> in : inMap.values()) {
		for (int i = 0; i < inMap.size(); i++) {
			for (String s1 : inMap.get(i)) {
			    if (!globals.contains(s1)) {
    				for (String s2 : inMap.get(i)) {
    					if (!(s1.equals(s2)) && !globals.contains(s2)) {
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
		currentMode = MODE.REG_ALLOC;
		for (currentInstruction = 0; currentInstruction < instructions.size(); currentInstruction++) {
			instructions.get(currentInstruction).accept(this);
		}
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
		if (currentMode == MODE.ANALYZE) {
			if (DEBUG) { System.out.println(node.getName()); }
			thisMethod = node;
			for (LLVarLocation var : node.getArgs()) {
				useMap.get(currentInstruction).add(var.getLabel());
			}
		} else if (currentMode == MODE.REG_ALLOC) {
			for (LLVarLocation var : node.getArgs()) {
				if (assignments.containsKey(var.getLabel())) {
					var.putInRegister(assignments.get(var.getLabel()));
				}
			}
		}
		
	}

	@Override
	public void visit(LLEnvironment node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLAssign node) {
		if (node.getLoc() instanceof LLVarLocation) {
			LLVarLocation loc = ((LLVarLocation)node.getLoc());
			String dest = loc.getLabel();
			if (currentMode == MODE.ANALYZE) {
				//currentSet.add(dest);
				defMap.get(currentInstruction).add(dest);
			} else if (currentMode == MODE.REG_ALLOC) {
				if (assignments.containsKey(dest)) {
					loc.putInRegister(assignments.get(dest));
				}
			} else if (currentMode == MODE.DEADCODE) {
			    // If the variable is not live after assigning it here, this is dead code
			    if (!(outMap.get(currentInstruction).contains(dest))) {
			        node.kill();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLVarLocation node) {
		if (currentMode == MODE.ANALYZE) {
			//currentSet.add(node.getLabel());
			useMap.get(currentInstruction).add(node.getLabel());
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
