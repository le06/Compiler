package edu.mit.compilers.codegen;

import java.io.Writer;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import edu.mit.compilers.checker.DecafChecker;
import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.checker.Ir.IrNode;
import edu.mit.compilers.codegen.ll.LLFile;

public class DecafUnoptomizedCodeGenerator {
    private CodeGenerator generator;
    private DecafChecker checker;
    private boolean debug = false;
    private boolean wasError = false;
    private LabelNamer lNamer;
    private AddressAssigner aAssign;
    private Ir ir;
    private LLFile file;
    
    public DecafUnoptomizedCodeGenerator(DecafChecker dc) {
        checker = dc;
        lNamer = new LabelNamer();
        aAssign = new AddressAssigner();
        generator = new CodeGenerator();
    }
    
    public void setTrace(boolean doDebug) {
        debug = doDebug;
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
        aAssign.assign(file);                   // Assign temp var addresses
        
        generator.outputASM(stream, file);      // Write actual ASM to stream
    }
    
    public boolean getError() {
        return wasError;
    }
}
