package edu.mit.compilers.checker;

import antlr.ASTFactory;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import edu.mit.compilers.checker.Ir.*;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.LineNumberedAST;

public class DecafChecker {
    private DecafParser parser;
    private boolean wasError;
    private boolean debug;
    
    public DecafChecker (DecafParser decaf_parser) {
        // Save the line/column numbers so we get meaningful parse data.
        ASTFactory factory = new ASTFactory();
        factory.setASTNodeClass(LineNumberedAST.class);
        parser = decaf_parser;
        parser.setASTFactory(factory);
    }
    
    public void check() throws RecognitionException, TokenStreamException {
        @SuppressWarnings("unused")
        Ir ir = generateIr();
        IrNodeChecker checker = new IrNodeChecker();
        ir.accept(checker);
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
