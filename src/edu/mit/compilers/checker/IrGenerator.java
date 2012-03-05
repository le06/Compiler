package edu.mit.compilers.checker;
import antlr.collections.AST;
import edu.mit.compilers.checker.Ir.*;
import edu.mit.compilers.grammar.DecafParserTokenTypes;

public class IrGenerator {
    /**
     * Translates an antlr generated AST into an IR
     * 
     * @param ast
     *         A node of an AST returned by an antlr grammar.
     * @return
     *         An Ir object generated by evaluating the type of the
     *         AST node, determining the related Ir class, and creating
     *         the object by analyzing the AST to fill the parameters
     *         of the returned object.
     */
    public static Ir fromAST(AST ast) {
        /*
         * This switch statement encompasses the logic of this function.
         * fromAST works recursively, looking at the type of the current node
         * in the AST and switching on its type, then returning a node of the Ir
         * after analyzing the children of the node to fill the parameters of the
         * associated Ir object.
         */
        Ir outIr; // The returned variable
        AST next; // Helper variable for traversing children
        
        switch (ast.getType()) {
        
        case DecafParserTokenTypes.TK_true:
            outIr = new IrBoolLiteral(true);
            break;
            
        case DecafParserTokenTypes.TK_false:
            outIr = new IrBoolLiteral(false);
            break;
            
        case DecafParserTokenTypes.TK_break:
            outIr = new IrBreakStmt();
            break;
            
        case DecafParserTokenTypes.TK_boolean:
            outIr = new IrType(IrType.Type.BOOLEAN);
            break;
            
        case DecafParserTokenTypes.TK_callout:
            // outIr = new IrCalloutStmt TODO
            return null;
            
        case DecafParserTokenTypes.TK_class:
            IrClassDecl cdecl = new IrClassDecl();
            
            next = ast.getFirstChild();
            for (int i = 0; i < ast.getNumberOfChildren(); i++) {
                cdecl.addMember((IrMemberDecl)fromAST(next));
                next = next.getNextSibling();
            }
            
             outIr = (Ir)cdecl;
            break;
        
        case DecafParserTokenTypes.TK_continue:
            outIr = new IrContinueStmt();
            break;
            
        case DecafParserTokenTypes.TK_for:
            next = ast.getFirstChild();
            
            IrIdentifier counter = (IrIdentifier)fromAST(next);
            next = next.getNextSibling();
            
            IrExpression start_value = (IrExpression)fromAST(next);
            next = next.getNextSibling();
            
            IrExpression stop_value = (IrExpression)fromAST(next);
            next = next.getNextSibling();
            
            IrBlock block = (IrBlock)fromAST(next);
            
            outIr = new IrForStmt(counter, start_value, stop_value, block);
            break;
            
        case DecafParserTokenTypes.TK_if:
            next = ast.getFirstChild();
            
            IrExpression test = (IrExpression)fromAST(next);
            
            next = next.getNextSibling();
            IrBlock if_block = (IrBlock)fromAST(next);
            
            next = next.getNextSibling();
            IrBlock else_block = (IrBlock)fromAST(next);
            
            outIr = new IrIfStmt(test, if_block, else_block);
            break;
            
        case DecafParserTokenTypes.TK_int:
            outIr = new IrType(IrType.Type.INT);
            break;
        
        case DecafParserTokenTypes.TK_return:
            outIr = new IrReturnStmt();
            break;
        
        case DecafParserTokenTypes.TK_void:
            outIr = new IrType(IrType.Type.VOID);
            break;

        case DecafParserTokenTypes.TK_while:
            next = ast.getFirstChild();
            
            IrExpression cond = (IrExpression)fromAST(next);
            
            next = next.getNextSibling();
            IrBlock true_block = (IrBlock)fromAST(next);
            
            outIr = new IrWhileStmt(cond, true_block);
            break;
        

        case DecafParserTokenTypes.NOT:
            outIr = new IrUnopExpr(IrUnaryOperator.NOT,
                         (IrExpression)fromAST(ast.getFirstChild()));
            break;
        

        case DecafParserTokenTypes.MINUS:
            outIr = new IrUnopExpr(IrUnaryOperator.MINUS,
                    (IrExpression)fromAST(ast.getFirstChild()));
            break;
        

        case DecafParserTokenTypes.MUL:
            outIr = parseBinOp(ast, IrBinOperator.MUL);
            break;
        

        case DecafParserTokenTypes.DIV:
            outIr = parseBinOp(ast, IrBinOperator.DIV);
            break;
        

        case DecafParserTokenTypes.MOD:
            outIr = parseBinOp(ast, IrBinOperator.MOD);
            break;
        

        case DecafParserTokenTypes.PLUS:
            outIr = parseBinOp(ast, IrBinOperator.PLUS);
            break;
        

        case DecafParserTokenTypes.LT:
            outIr = parseBinOp(ast, IrBinOperator.LT);
            break;
        

        case DecafParserTokenTypes.GT:
            outIr = parseBinOp(ast, IrBinOperator.GT);
            break;
        

        case DecafParserTokenTypes.LEQ:
            outIr = parseBinOp(ast, IrBinOperator.LEQ);
            break;
        

        case DecafParserTokenTypes.GEQ:
            outIr = parseBinOp(ast, IrBinOperator.GEQ);
            break;
        

        case DecafParserTokenTypes.EQ:
            outIr = parseBinOp(ast, IrBinOperator.EQ);
            break;
        

        case DecafParserTokenTypes.NEQ:
            outIr = parseBinOp(ast, IrBinOperator.NEQ);
            break;
        

        case DecafParserTokenTypes.AND:
            outIr = parseBinOp(ast, IrBinOperator.AND);
            break;
        

        case DecafParserTokenTypes.OR:
            outIr = parseBinOp(ast, IrBinOperator.OR);
            break;
        

        case DecafParserTokenTypes.ASSIGN:
            next = ast.getFirstChild();
            outIr = new IrAssignStmt((IrLocation)fromAST(next),
                                     (IrExpression)fromAST(next.getNextSibling()));
            break;
        
        case DecafParserTokenTypes.INC_ASSIGN:
            next = ast.getFirstChild();
            outIr = new IrPlusAssignStmt((IrLocation)fromAST(next),
                                     (IrExpression)fromAST(next.getNextSibling()));
            break;
        
        case DecafParserTokenTypes.DEC_ASSIGN:
            next = ast.getFirstChild();
            outIr = new IrMinusAssignStmt((IrLocation)fromAST(next),
                                     (IrExpression)fromAST(next.getNextSibling()));
            break;
        
        case DecafParserTokenTypes.ID:
            outIr = new IrIdentifier(ast.getText());
            break;

        case DecafParserTokenTypes.DEC_LITERAL:
            outIr = new IrIntLiteral(ast.getText(), IrIntLiteral.Type.DECIMAL);
            break;

        case DecafParserTokenTypes.HEX_LITERAL:
            outIr = new IrIntLiteral(ast.getText(), IrIntLiteral.Type.HEX);
            break;

        case DecafParserTokenTypes.BIN_LITERAL:
            outIr = new IrIntLiteral(ast.getText(), IrIntLiteral.Type.BINARY);
            break;

        case DecafParserTokenTypes.CHAR:
            outIr = IrCharLiteral.fromString(ast.getText());
            break;

        case DecafParserTokenTypes.STRING:
            outIr = new IrStringLiteral(ast.getText());
            break;
        
        default:
            return null;
        }
        
        outIr.addLocInfo(ast.getLine(), ast.getColumn());
        
        return outIr;
    }
    
    private static Ir parseBinOp(AST node, IrBinOperator op) {
        AST l = node.getFirstChild();
        AST r = l.getNextSibling();
        return new IrBinopExpr(op,
                              (IrExpression)fromAST(l),
                              (IrExpression)fromAST(r));
    }
}
