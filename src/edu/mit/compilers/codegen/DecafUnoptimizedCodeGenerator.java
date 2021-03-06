package edu.mit.compilers.codegen;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import edu.mit.compilers.checker.DecafChecker;
import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.checker.Ir.IrNode;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.LabelNamer;
import edu.mit.compilers.optimizer.DecafOptimizer;
import edu.mit.compilers.optimizer.RegAlloc;

public class DecafUnoptimizedCodeGenerator {
    private CodeGenerator generator;
    private DecafChecker checker;
    private boolean debug = false;
    private boolean wasError = false;
    private LabelNamer lNamer;
    private AddressAssigner aAssign;
    private Ir ir;
    private LLFile file;
    
    public DecafUnoptimizedCodeGenerator(DecafChecker dc) {
        checker = dc;
        lNamer = new LabelNamer();
/*        aAssign = new AddressAssigner();
        generator = new CodeGenerator();*/
        
    }
    
    public void setTrace(boolean doDebug) {
        debug = doDebug;
    }
    
    public LLFile getLLRep() {
        return file;
    }
    
    public void gen(Writer stream) throws RecognitionException, TokenStreamException {
        checker.check();                       // Check semantics
        if (checker.getError()) {              // Check error before continuing
            wasError = true;
            return;
        }
        
        ir = checker.getIr();                   // Get Ir
        file = (LLFile)ir.getllRep(null, null); // Convert to LL
        lNamer.name(file);                      // Make labels unique
        
        CfgGen g = new CfgGen();
        g.generateCFG(file);
        ArrayList<BasicBlock> blocks = g.getBlocksInOrder();
        
        ArrayList<LLNode> instrs = new ArrayList<LLNode>();
        ArrayList<ArrayList<LLNode>> methods = new ArrayList<ArrayList<LLNode>>();
        ArrayList<LLNode> currentMethod = new ArrayList<LLNode>();
        
        for (BasicBlock b : blocks) {
        	for (LLNode n : b.getInstructions()) {
        		if (n instanceof LLMethodDecl) {
        			methods.add(currentMethod);
        			currentMethod = new ArrayList<LLNode>();
        		}
        		
        		currentMethod.add(n);
        	}
        	instrs.addAll(b.getInstructions());
        }
        methods.add(currentMethod);
        
        
        if (debug) {
            for (LLNode n : instrs) {
                System.out.println(n.toString());
            }
        }

        AddressAssign a = new AddressAssign();
        HashMap<String, Integer> methodMap = a.assign(instrs);
        
        RegAlloc r;
        for (ArrayList<LLNode> method : methods) {
        	r = new RegAlloc();
        	r.allocMethod(method, a.getGlobals());
        }
        
/*        DecafOptimizer o = new DecafOptimizer();
        o.optimize(blocks.get(0));*/
        
        CodeGen c = new CodeGen();
        c.gen(instrs, methodMap, stream);
    }
    
    public boolean getError() {
        return wasError;
    }
}
