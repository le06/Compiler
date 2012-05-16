package edu.mit.compilers.codegen;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.checker.Ir.IrArrayDecl;
import edu.mit.compilers.checker.Ir.IrArrayLocation;
import edu.mit.compilers.checker.Ir.IrAssignStmt;
import edu.mit.compilers.checker.Ir.IrBaseDecl;
import edu.mit.compilers.checker.Ir.IrBinopExpr;
import edu.mit.compilers.checker.Ir.IrBlock;
import edu.mit.compilers.checker.Ir.IrBlockStmt;
import edu.mit.compilers.checker.Ir.IrBoolLiteral;
import edu.mit.compilers.checker.Ir.IrBreakStmt;
import edu.mit.compilers.checker.Ir.IrCalloutStmt;
import edu.mit.compilers.checker.Ir.IrClassDecl;
import edu.mit.compilers.checker.Ir.IrContinueStmt;
import edu.mit.compilers.checker.Ir.IrExpression;
import edu.mit.compilers.checker.Ir.IrFieldDecl;
import edu.mit.compilers.checker.Ir.IrForStmt;
import edu.mit.compilers.checker.Ir.IrGlobalDecl;
import edu.mit.compilers.checker.Ir.IrIdentifier;
import edu.mit.compilers.checker.Ir.IrIfStmt;
import edu.mit.compilers.checker.Ir.IrIntLiteral;
import edu.mit.compilers.checker.Ir.IrLocalDecl;
import edu.mit.compilers.checker.Ir.IrMemberDecl;
import edu.mit.compilers.checker.Ir.IrMethodCallStmt;
import edu.mit.compilers.checker.Ir.IrMethodDecl;
import edu.mit.compilers.checker.Ir.IrMinusAssignStmt;
import edu.mit.compilers.checker.Ir.IrNodeVisitor;
import edu.mit.compilers.checker.Ir.IrParameterDecl;
import edu.mit.compilers.checker.Ir.IrPlusAssignStmt;
import edu.mit.compilers.checker.Ir.IrReturnStmt;
import edu.mit.compilers.checker.Ir.IrStatement;
import edu.mit.compilers.checker.Ir.IrUnopExpr;
import edu.mit.compilers.checker.Ir.IrVarDecl;
import edu.mit.compilers.checker.Ir.IrVarLocation;
import edu.mit.compilers.checker.Ir.IrWhileStmt;
import edu.mit.compilers.codegen.ll2.*;
import edu.mit.compilers.codegen.ll2.LlExpression.Type;
import edu.mit.compilers.codegen.ll2.LlJmp.JumpType;

public class LlGenerator implements IrNodeVisitor {

    private Ir ir;
    private LlNode ll;
    
    /*
     * The state of the LL generator as it walks the IR tree.
     */
    private LlNode parent;
    private LlLabel breakPoint;
    private LlLabel continuePoint;
    private LlLocation currentLoc; // a location in memory. for example: the LHS in some assign statement.
    private LlExpression currentLit; // an integer literal or a boolean literal.
    private int currently_evaluating_expr = 0; // "true" if non-zero. like a semaphore.
    private LlEnv expr_env; // the series of instructions required to fully evaluate an entire expression.
    private Type currentType;
    private int currentTemp = 1;
    
    // expects the root node as input.
    public LlGenerator(Ir ir) {
        this.ir = ir;
    }
    
    public LlNode generateLL() {
        ir.accept(this);
        return ll;
    }
    
    @Override
    public void visit(IrClassDecl node) {
        LlProgram program = new LlProgram();
        ll = (LlNode)program;
        parent = ll;
        
        for (IrMemberDecl m : node.getMembers()) {
            m.accept(this);
        }
    }

    @Override
    public void visit(IrFieldDecl node) {
        for (IrGlobalDecl g : node.getGlobals()) {
            g.accept(this);
        }
    }

    @Override
    public void visit(IrBaseDecl node) {
        LlProgram program = (LlProgram)parent;
        LlLabel g = new LlLabel(node.getSymbol());
        
        LlGlobalDecl d = new LlGlobalDecl(g);
        program.addGlobalDecl(d);

    }

    @Override
    public void visit(IrArrayDecl node) {
        LlProgram program = (LlProgram)parent;
        LlLabel g = new LlLabel(node.getSymbol());
        
        LlArrayDecl d = new LlArrayDecl(g, node.getArraySize().getIntRep());
        program.addArrayDecl(d);
    }

    @Override
    public void visit(IrMethodDecl node) {
        LlProgram program = (LlProgram)parent; // the old parent.
        LlEnv method_code = new LlEnv();
        
        currentTemp = 1;
        parent = (LlNode)method_code; // the new parent.
        node.getBlock().accept(this);
        
        String name = node.getId().getId();
        int num_args = node.getParams().size();
        LlMethodDecl m;
        
        // set the method's return type.
        switch (node.getReturnType().myType) {
        case BOOLEAN:
            m = new LlMethodDecl(Type.BOOLEAN, name, num_args, method_code);
            break;
        case INT:
            m = new LlMethodDecl(Type.INT, name, num_args, method_code);
            break;
        case VOID:
            m = new LlMethodDecl(Type.VOID, name, num_args, method_code);
            method_code.addNode(new LlReturn(new LlIntLiteral(0)));
            break;
        default:
            throw new RuntimeException("Cannot have a method with mixed type");
        }
        
        // add the method params.
        for (IrParameterDecl p : node.getParams()) {
            m.addArg(p.getId().getSymbol());
            /*
            switch (p.getType().myType) {
            case BOOLEAN:
                m.addArg(Type.BOOLEAN, p.getId().getSymbol());
                break;
            case INT:
                m.addArg(Type.INT, p.getId().getSymbol());
                break;
            default:
                throw new RuntimeException("Unrecognized param type");
            }
            */
        }
        
        if (name == "main") {
            program.setMain(m);
        } else {
            program.addMethod(m);
        }
        
        parent = (LlNode)program;
    }

    // For the cases below, parent is an instance of LlEnvironment.
    
    @Override
    public void visit(IrBlock node) {
        for (IrVarDecl d : node.getVarDecls()) {
            d.accept(this);
        }
        for (IrStatement s : node.getStatements()) {
            s.accept(this);
        }
    }
    
    @Override
    public void visit(IrVarDecl node) {
        switch (node.getType().myType) {
        case BOOLEAN:
            currentType = Type.BOOLEAN;
            break;
        case INT:
            currentType = Type.INT;
            break;
        default:
            throw new RuntimeException("Unrecognized local var type");
        }

        for (IrLocalDecl d : node.getLocals()) {
            d.accept(this);
        }

        currentType = null;
    }

    @Override
    public void visit(IrLocalDecl node) {        
        String symbol = node.getSymbol();
        LlLocation loc = new LlTempLoc(symbol);
        LlExpression expr = new LlIntLiteral(0);
        LlAssign a = new LlAssign(loc, expr);
        
        LlEnv env = (LlEnv)parent;
        env.addNode(a);
    }

    @Override
    public void visit(IrBlockStmt node) {
        node.getBlock().accept(this);
    }

    @Override
    public void visit(IrBreakStmt node) {
        LlEnv env = (LlEnv)parent;
        env.addNode(new LlJmp(JumpType.UNCONDITIONAL, breakPoint));
    }    
    
    @Override
    public void visit(IrContinueStmt node) {
        LlEnv env = (LlEnv)parent;
        env.addNode(new LlJmp(JumpType.UNCONDITIONAL, continuePoint));
    }

    @Override
    public void visit(IrReturnStmt node) {
        IrExpression expr = node.getReturnExpr();
        LlReturn r;
        if (expr == null) {
            r = new LlReturn();
        } else {
            // TODO: add code to determine if return expr is a single loc, expr (that needs to be flattened), or constant
            // if expr needs evaluation, add that code to the env also.
            expr.accept(this);
            if (currentLoc != null) {
                r = new LlReturn(currentLoc);
            }
            else if (currentLit != null){
                r = new LlReturn(currentLit);
            } else {
                throw new RuntimeException("Cannot have a method with mixed type");
            }
            currentLoc = null;
            currentLit = null;
        }
        
        LlEnv env = (LlEnv)parent;
        env.addNode(r);
    }

    // The basic building blocks of an expression.
    
    @Override
    public void visit(IrIdentifier node) {
        processVarLocation(node.getSymbol());
    }
    
    @Override
    public void visit(IrVarLocation node) {
        processVarLocation(node.getSymbol());
    }

    private void processVarLocation(String symbol) {
        char prefix = symbol.charAt(0);
        switch (prefix) {
        case 'g':
            currentLoc = new LlGlobalLoc(symbol);
            break;
        case 'v':
        case 't':
            currentLoc = new LlTempLoc(symbol);
            break;
        default:
            throw new RuntimeException("Unrecognized symbol, expected single variable");
        }
    }
    
    @Override
    public void visit(IrIntLiteral node) {
        long intVal = node.getIntRep();
        currentLit = new LlIntLiteral(intVal);
    }

    @Override
    public void visit(IrBoolLiteral node) {
        boolean boolVal = node.getValue();
        currentLit = new LlBoolLiteral(boolVal);
    }
    
    // These exprs may or may not require evaluation of subexprs.
    
    @Override
    public void visit(IrArrayLocation node) {
        String symbol = node.getSymbol();
        IrExpression expr = node.getIndex();
        expr.accept(this);
        if (currentLoc != null) {
            LlLocation offset_loc = currentLoc;
            currentLoc = new LlArrayLoc(symbol, offset_loc);
        } else if (currentLit != null) {
            long offset = ((IrIntLiteral)currentLit).getIntRep();
            currentLoc = new LlArrayLoc(symbol, offset);
        } else {
            throw new RuntimeException("Unable to process array index");
        }

    }
    
    @Override
    public void visit(IrMethodCallStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrCalloutStmt node) {
        // TODO Auto-generated method stub

    }

    // These exprs guarantee the generation of temp vars, except in certain optimized cases.
    
    @Override
    public void visit(IrBinopExpr node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrUnopExpr node) {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void visit(IrWhileStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrForStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrIfStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrAssignStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrPlusAssignStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrMinusAssignStmt node) {
        // TODO Auto-generated method stub

    }

}
