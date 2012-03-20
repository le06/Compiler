package edu.mit.compilers.codegen;

import java.io.Writer;

import edu.mit.compilers.checker.DecafChecker;
import edu.mit.compilers.checker.Ir.IrNode;

public class DecafUnoptomizedCodeGenerator {
    private CodeGenerator generator;
    private DecafChecker checker;
    private boolean debug = false;
    private boolean wasError = false;
    private Writer outputStream;
    private LabelNamer lNamer;
    
    public DecafUnoptomizedCodeGenerator(DecafChecker dc, Writer stream) {
        checker = dc;
        outputStream = stream;
    }
    
    public void setTrace(boolean doDebug) {
        debug = doDebug;
    }
    
    public void gen() {
        // Check
        // Get Ir
        // Convert to LL
        // Name Labels
    }
    
    public boolean getError() {
        return wasError;
    }
}
