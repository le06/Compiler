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
import edu.mit.compilers.checker.Ir.IrBreakStmt;
import edu.mit.compilers.checker.Ir.IrCalloutStmt;
import edu.mit.compilers.checker.Ir.IrClassDecl;
import edu.mit.compilers.checker.Ir.IrContinueStmt;
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

public class LlGenerator implements IrNodeVisitor {

    private Ir ir;
    private LlNode ll;
    
    // the state of the LL generator as it walks the IR tree.
    private LlNode parent;
    private LlLabel break_point;
    private LlLabel continue_point;
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
        LlEnvironment method_code = new LlEnvironment();
        
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
            break;
        default:
            throw new RuntimeException("Cannot have a method with mixed type");
        }
        
        // add the method params.
        for (IrParameterDecl p : node.getParams()) {
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
        LlLocation loc = new LlTempLoc(currentType, symbol);
        LlExpression expr = new LlIntLiteral(0);
        LlAssign a = new LlAssign(loc, expr);
        
        LlEnvironment env = (LlEnvironment)parent;
        env.addNode(a);
    }

    @Override
    public void visit(IrBlockStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrContinueStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrBreakStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrReturnStmt node) {
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
    public void visit(IrMethodCallStmt node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrCalloutStmt node) {
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

    @Override
    public void visit(IrVarLocation node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrArrayLocation node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrBinopExpr node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrUnopExpr node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrIntLiteral node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IrIdentifier node) {
        // TODO Auto-generated method stub

    }

}
