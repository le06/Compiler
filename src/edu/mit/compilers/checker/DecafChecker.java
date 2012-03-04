package edu.mit.compilers.checker;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.grammar.DecafParser;

public class DecafChecker {
    private DecafParser parser;
    private boolean wasError;
    private boolean debug;
    
    public DecafChecker (DecafParser decaf_parser) {
        parser = decaf_parser;
    }
    
    public void check() throws RecognitionException, TokenStreamException {
        Ir ir = generateIr();
    }
    
    public boolean getError() {
        return wasError;
    }
    
    public void setTrace(boolean do_debug) {
        debug = do_debug;
    }
    
    private Ir generateIr() throws RecognitionException, TokenStreamException {
        parser.program();
        wasError |= parser.getError();
        AST ast = parser.getAST();
        return IrGenerator.fromAST(ast);
    }
}
