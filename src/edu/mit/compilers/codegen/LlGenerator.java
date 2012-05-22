package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.mit.compilers.checker.Ir.Ir;
import edu.mit.compilers.checker.Ir.IrArrayDecl;
import edu.mit.compilers.checker.Ir.IrArrayLocation;
import edu.mit.compilers.checker.Ir.IrAssignStmt;
import edu.mit.compilers.checker.Ir.IrBaseDecl;
import edu.mit.compilers.checker.Ir.IrBinOperator;
import edu.mit.compilers.checker.Ir.IrBinopExpr;
import edu.mit.compilers.checker.Ir.IrBlock;
import edu.mit.compilers.checker.Ir.IrBlockStmt;
import edu.mit.compilers.checker.Ir.IrBoolLiteral;
import edu.mit.compilers.checker.Ir.IrBreakStmt;
import edu.mit.compilers.checker.Ir.IrCalloutArg;
import edu.mit.compilers.checker.Ir.IrCalloutStmt;
import edu.mit.compilers.checker.Ir.IrCharLiteral;
import edu.mit.compilers.checker.Ir.IrClassDecl;
import edu.mit.compilers.checker.Ir.IrContinueStmt;
import edu.mit.compilers.checker.Ir.IrExprArg;
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
import edu.mit.compilers.checker.Ir.IrNode;
import edu.mit.compilers.checker.Ir.IrNodeVisitor;
import edu.mit.compilers.checker.Ir.IrParameterDecl;
import edu.mit.compilers.checker.Ir.IrPlusAssignStmt;
import edu.mit.compilers.checker.Ir.IrReturnStmt;
import edu.mit.compilers.checker.Ir.IrStatement;
import edu.mit.compilers.checker.Ir.IrStringArg;
import edu.mit.compilers.checker.Ir.IrStringLiteral;
import edu.mit.compilers.checker.Ir.IrType;
import edu.mit.compilers.checker.Ir.IrUnopExpr;
import edu.mit.compilers.checker.Ir.IrVarDecl;
import edu.mit.compilers.checker.Ir.IrVarLocation;
import edu.mit.compilers.checker.Ir.IrWhileStmt;
import edu.mit.compilers.codegen.ll2.*;
import edu.mit.compilers.codegen.ll2.LlAnnotation.AnnotationType;
import edu.mit.compilers.codegen.ll2.LlConstant.Type;
import edu.mit.compilers.codegen.ll2.LlMethodDecl.MethodType;
import edu.mit.compilers.codegen.ll2.LlJmp.JumpType;

public class LlGenerator implements IrNodeVisitor {

    private Ir ir;
    private LlNode ll;
        
    /*
     * The state of the LL generator as it walks the IR tree.
     */
    private LlProgram program;
    private LlEnv currentEnv; // the current environment where instructions should be added.
    private LlLocation currentLoc; // a location in memory. for example: the LHS in some assign statement.
    private LlConstant currentLit; // an integer literal or a boolean literal.
    private int currentlyEvaluatingExpr = 0; // "true" if non-zero. like a semaphore.

    private int currentTemp = 1;
    
    private int stringLitCounter = 1;
    private String stringLitPrefix = "strlit_";
    private ArrayList<LlStringLiteral> stringLiterals = new ArrayList<LlStringLiteral>();

    private int ifCounter = 1;
    private int forCounter = 1;
    private int whileCounter = 1;
    private int ssCounter = 1;
    
    private LlLabel breakPoint;
    private LlLabel continuePoint;
    
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
        program = new LlProgram();
        
        for (IrMemberDecl m : node.getMembers()) {
            m.accept(this);
        }
        
        for (LlStringLiteral s : stringLiterals) {
            program.addString(s);
        }
        
        ll = (LlNode)program;
    }

    @Override
    public void visit(IrFieldDecl node) {
        for (IrGlobalDecl g : node.getGlobals()) {
            g.accept(this);
        }
    }

    @Override
    public void visit(IrBaseDecl node) {
        LlLabel g = new LlLabel(node.getSymbol());
        
        LlGlobalDecl d = new LlGlobalDecl(g);
        program.addGlobalDecl(d);

    }

    @Override
    public void visit(IrArrayDecl node) {
        LlLabel g = new LlLabel(node.getSymbol());
        
        LlArrayDecl d = new LlArrayDecl(g, node.getArraySize().getIntRep());
        program.addArrayDecl(d);
    }

    @Override
    public void visit(IrMethodDecl node) {
        currentEnv = new LlEnv();
        
        // walk the method contents.
        node.getBlock().accept(this);
        
        String name = node.getId().getId();
        int num_args = node.getParams().size();
        LlMethodDecl m;
        
        // set the method's return type.
        switch (node.getReturnType().myType) {
        case BOOLEAN:
            m = new LlMethodDecl(MethodType.BOOLEAN, name, num_args, currentEnv);
            break;
        case INT:
            m = new LlMethodDecl(MethodType.INT, name, num_args, currentEnv);
            break;
        case VOID:
            m = new LlMethodDecl(MethodType.VOID, name, num_args, currentEnv);
            currentEnv.addNode(new LlReturn(new LlIntLiteral(0)));
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
        /*
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
        */
        for (IrLocalDecl d : node.getLocals()) {
            d.accept(this);
        }

        //currentType = null;
    }

    @Override
    public void visit(IrLocalDecl node) {        
        String symbol = node.getSymbol();
        LlLocation loc = new LlTempLoc(symbol);
        LlConstant expr = new LlIntLiteral(0);
        
        LlAssign a = new LlAssign(loc, expr, Type.INT, false);
        currentEnv.addNode(a);
    }

    @Override
    public void visit(IrBlockStmt node) {
        node.getBlock().accept(this);
    }

    @Override
    public void visit(IrReturnStmt node) {
        IrExpression expr = node.getReturnExpr();
        LlReturn r;
        if (expr == null) {
            r = new LlReturn();
        } else {
            acceptExpr(expr);
            
            if (currentLoc != null) {
                r = new LlReturn(currentLoc);
            }
            else if (currentLit != null){
                r = new LlReturn(currentLit);
            } else {
                throw new RuntimeException("Cannot have a method with mixed type");
            }
        }
        
        currentEnv.addNode(r);
    }

    // The basic building blocks of an expression.

    private void acceptExpr(IrNode expr) {
        currentLoc = null;
        currentLit = null;
        currentlyEvaluatingExpr++;
        expr.accept(this);
        currentlyEvaluatingExpr--;
    }
    
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
    public void visit(IrCharLiteral node) {
        char literal = node.getLiteral();
        currentLit = new LlIntLiteral((long)literal);
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
        
        acceptExpr(expr);
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
    
    // TODO: control flow transfers to new method scope -> new basic block.
    
    @Override
    public void visit(IrMethodCallStmt node) {
        LlMethodCall mc;
        
        switch (node.getType().myType) {
        case BOOLEAN:
            mc = new LlMethodCall(MethodType.BOOLEAN, node.getMethodName().getId());
            break;
        case INT:
            mc = new LlMethodCall(MethodType.INT, node.getMethodName().getId());
            break;
        case VOID:
            mc = new LlMethodCall(MethodType.VOID, node.getMethodName().getId());
            break;
        default:
            throw new RuntimeException("Invalid return type");
        }
        
        for (IrExpression arg : node.getArgs()) {
            acceptExpr(arg);
            if (currentLoc != null) {
                mc.addParam(currentLoc);
            } else if (currentLit != null) {
                mc.addParam(currentLit);
            } else {
                throw new RuntimeException("Invalid method argument");
            }
        }
        
        currentEnv.addNode(mc);
        if (currentlyEvaluatingExpr > 0) {
            currentLoc = mc.getReturnLocation();
        }
    }

    @Override
    public void visit(IrCalloutStmt node) {

        LlCallout co = new LlCallout(node.getFunctionName().toString());
        
        for (IrCalloutArg arg : node.getArgs()) {
            acceptExpr(arg);
            if (currentLoc != null) {
                co.addParam(currentLoc);
            } else if (currentLit != null) {
                co.addParam(currentLit);
            } else {
                throw new RuntimeException("Invalid callout argument");
            }
        }
        
        currentEnv.addNode(co);
        if (currentlyEvaluatingExpr > 0) {
            currentLoc = co.getReturnLocation();
        }
        
    }

    @Override
    public void visit(IrExprArg node) {
        node.getArg().accept(this);
    }
    
    @Override
    public void visit(IrStringArg node) {
        String msg = node.getArg().toString();
        String label = stringLitPrefix + String.valueOf(stringLitCounter);
        stringLitCounter++;
        LlStringLiteral strLit = new LlStringLiteral(new LlLabel(label), msg);
        
        stringLiterals.add(strLit);
        currentLit = strLit;
    }
    
    // These exprs guarantee the generation of temp vars, except in certain optimized cases.
    // Conditional operators are converted to short circuits. See LlAnnotation.
    
    @Override
    public void visit(IrBinopExpr node) {
        LlTempLoc result = new LlTempLoc("t" + String.valueOf(currentTemp));
        currentTemp++;
        
        // if the op is && or ||, convert into a short circuit representation.
        IrBinOperator op = node.getOperator();
        if (op == IrBinOperator.AND) {
            LlAnnotation start = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss" + String.valueOf(ssCounter), false, null);
            LlAnnotation end = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss" + String.valueOf(ssCounter), true, null);
            LlLabel ss = new LlLabel("ss_" + String.valueOf(ssCounter));
            LlLabel ssEnd = new LlLabel("ssend_" + String.valueOf(ssCounter));
            
            ssCounter++;
            currentEnv.addNode(start);
            
            LlAssign assign;
            LlCmp cmp;
            String leftSymbol, rightSymbol;
            
            // eval left first.
            acceptExpr(node.getLeft());
            if (currentLoc != null) {
                cmp = new LlCmp(currentLoc);
                leftSymbol = currentLoc.getSymbol();
            } else if (currentLit != null) {
                cmp = new LlCmp(currentLit);
                leftSymbol = currentLit.print();
            } else {
                throw new RuntimeException("LHS of binop badly formed");
            }
            
            // short circuits if left == 0 == false.
            currentEnv.addNode(cmp);
            currentEnv.addNode(new LlJmp(JumpType.EQUAL,ss));
            
            // if short circuit fails, eval right.
            acceptExpr(node.getRight());
            if (currentLoc != null) {
                assign = new LlAssign(result, currentLoc, Type.BOOLEAN, false);
                rightSymbol = currentLoc.getSymbol();
            } else if (currentLit != null) {
                assign = new LlAssign(result, currentLit, Type.BOOLEAN, false);
                rightSymbol = currentLit.print();
            } else {
                throw new RuntimeException("RHS of binop badly formed");
            }
            
            // result = right;
            currentEnv.addNode(assign);
            currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL,ssEnd));
            
            // if short-circuit, result = 0;
            currentEnv.addNode(ss);
            currentEnv.addNode(new LlAssign(result, new LlIntLiteral(0), Type.BOOLEAN, false));
            currentEnv.addNode(ssEnd);
            currentEnv.addNode(end);
            
            // annotate beginning and end of short-circuit code.
            String annotation = result.getSymbol() + "=" + leftSymbol + "&&" + rightSymbol;
            start.setAnnotation(annotation);
            end.setAnnotation(annotation);
            
        } else if (op == IrBinOperator.OR) {
            LlAnnotation start = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss" + String.valueOf(ssCounter), false, null);
            LlAnnotation end = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss" + String.valueOf(ssCounter), true, null);
            LlLabel ss = new LlLabel("ss_" + String.valueOf(ssCounter));
            LlLabel ssEnd = new LlLabel("ssend_" + String.valueOf(ssCounter));
            
            ssCounter++;
            currentEnv.addNode(start);
            
            LlAssign assign;
            LlCmp cmp;
            String leftSymbol, rightSymbol;
            
            // eval left first.
            acceptExpr(node.getLeft());
            if (currentLoc != null) {
                cmp = new LlCmp(currentLoc);
                leftSymbol = currentLoc.getSymbol();
            } else if (currentLit != null) {
                cmp = new LlCmp(currentLit);
                leftSymbol = currentLit.print();
            } else {
                throw new RuntimeException("LHS of binop badly formed");
            }
            
            // short circuits if left == 1 == true.
            currentEnv.addNode(cmp); // cmp 0, left
            currentEnv.addNode(new LlJmp(JumpType.NOT_EQUAL,ss)); // 0 != 1
            
            // if short circuit fails, eval right.
            acceptExpr(node.getRight());
            if (currentLoc != null) {
                assign = new LlAssign(result, currentLoc, Type.BOOLEAN, false);
                rightSymbol = currentLoc.getSymbol();
            } else if (currentLit != null) {
                assign = new LlAssign(result, currentLit, Type.BOOLEAN, false);
                rightSymbol = currentLit.print();
            } else {
                throw new RuntimeException("RHS of binop badly formed");
            }
            
            // result = right;
            currentEnv.addNode(assign);
            currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL,ssEnd));
            
            // if short-circuit, result = 1;
            currentEnv.addNode(ss);
            currentEnv.addNode(new LlAssign(result, new LlIntLiteral(1), Type.BOOLEAN, false));
            currentEnv.addNode(ssEnd);
            currentEnv.addNode(end);
            
            // annotate beginning and end of short-circuit code.
            String annotation = result.getSymbol() + "=" + leftSymbol + "||" + rightSymbol;
            start.setAnnotation(annotation);
            end.setAnnotation(annotation);
            
        } else {
            // normal binop, otherwise.
            LlLocation lhsLoc, rhsLoc;
            LlConstant lhsLit, rhsLit;
            
            // walk the left expr subtree.
            acceptExpr(node.getLeft());
            if (currentLoc != null) {
                lhsLoc = currentLoc;
                lhsLit = null;
            } else if (currentLit != null) {
                lhsLit = currentLit;
                lhsLoc = null;
            } else {
                throw new RuntimeException("LHS of expression badly formed");
            }
            // walk the right expr subtree.
            acceptExpr(node.getRight());
            if (currentLoc != null) {
                rhsLoc = currentLoc;
                rhsLit = null;
            } else if (currentLit != null) {
                rhsLit = currentLit;
                rhsLoc = null;
            } else {
                throw new RuntimeException("RHS of expression badly formed");
            }
            
            Type type;
            switch (node.getType().myType) {
            case BOOLEAN:
                type = Type.BOOLEAN;
                break;
            case INT:
                type = Type.INT;
                break;
            default:
                throw new RuntimeException("Unrecognized local var type");
            }
            
            LlBinaryAssign a;
            if (lhsLoc != null) {
                if (rhsLoc != null) {
                    a = new LlBinaryAssign(result, lhsLoc, rhsLoc, type, op);
                } else if (rhsLit != null) {
                    a = new LlBinaryAssign(result, lhsLoc, rhsLit, type, op);
                } else {
                    throw new RuntimeException("Bad binary expr.");
                }
                
            } else if (lhsLit != null ){
                if (rhsLoc != null) {
                    a = new LlBinaryAssign(result, lhsLit, rhsLoc, type, op);
                } else if (rhsLit != null) {
                    a = new LlBinaryAssign(result, lhsLit, rhsLit, type, op);
                } else {
                    throw new RuntimeException("Bad binary expr.");
                }
                
            } else {
                throw new RuntimeException("Bad binary expr.");
            }
            
            currentEnv.addNode(a);    
        }
        
        currentLoc = result;
        currentLit = null;
    }

    @Override
    public void visit(IrUnopExpr node) {
        LlTempLoc result = new LlTempLoc("t" + String.valueOf(currentTemp));
        currentTemp++;
        
        Type type;
        switch (node.getOperator()) {
        case MINUS:
            type = Type.INT;
            break;
        case NOT:
            type = Type.BOOLEAN;
            break;
        default:
            throw new RuntimeException("Unrecognized unary operator");
        }
        
        LlAssign a;
        acceptExpr(node.getExpr());
        if (currentLoc != null) {
            a = new LlAssign(result, currentLoc, type, true);
        } else if (currentLit != null) {
            a = new LlAssign(result, currentLit, type, true);
        } else {
            throw new RuntimeException("Unary expr badly formed");
        }

        currentEnv.addNode(a);
        currentLoc = result;
        currentLit = null;
    }
    
    // Value assignment.
    
    @Override
    public void visit(IrAssignStmt node) {
        node.getLeft().accept(this);
        LlLocation loc = currentLoc;
        
        LlAssign a;
        acceptExpr(node.getRight());
        if (currentLoc != null) {
            a = new LlAssign(loc, currentLoc, null, true);
        } else if (currentLit != null) {
            a = new LlAssign(loc, currentLit, null, true);
        } else {
            throw new RuntimeException("Unary expr badly formed");
        }

        currentEnv.addNode(a);
    }

    @Override
    public void visit(IrPlusAssignStmt node) {
        node.getLeft().accept(this);
        LlLocation loc = currentLoc;
        
        LlBinaryAssign a;
        acceptExpr(node.getRight());
        if (currentLoc != null) {
            a = new LlBinaryAssign(loc, loc, currentLoc, Type.INT, IrBinOperator.PLUS);
        } else if (currentLit != null) {
            a = new LlBinaryAssign(loc, loc, currentLit, Type.INT, IrBinOperator.PLUS);
        } else {
            throw new RuntimeException("Unary expr badly formed");
        }

        currentEnv.addNode(a);
    }

    @Override
    public void visit(IrMinusAssignStmt node) {
        node.getLeft().accept(this);
        LlLocation loc = currentLoc;
        
        LlBinaryAssign a;
        acceptExpr(node.getRight());
        if (currentLoc != null) {
            a = new LlBinaryAssign(loc, loc, currentLoc, Type.INT, IrBinOperator.MINUS);
        } else if (currentLit != null) {
            a = new LlBinaryAssign(loc, loc, currentLit, Type.INT, IrBinOperator.MINUS);
        } else {
            throw new RuntimeException("Unary expr badly formed");
        }

        currentEnv.addNode(a);
    }
    
    // Control flow.

    // TODO: control flow changes -> new basic blocks.
    
    @Override
    public void visit(IrIfStmt node) {
        LlAnnotation start = new LlAnnotation(AnnotationType.IF,
                "if_" + String.valueOf(ifCounter), false, null);
        LlAnnotation end = new LlAnnotation(AnnotationType.IF,
                "if_" + String.valueOf(ifCounter), true, null);
        LlLabel ifElse = new LlLabel("ifelse_" + String.valueOf(ifCounter));
        LlLabel ifEnd = new LlLabel("ifend_" + String.valueOf(ifCounter));
        
        ifCounter++;
        currentEnv.addNode(start);
        
        LlCmp cmp;
        
        IrExpression cond = node.getCondition();
        acceptExpr(cond);
        if (currentLoc != null) {
            cmp = new LlCmp(currentLoc);
        } else if (currentLit != null) {
            cmp = new LlCmp(currentLit);
        } else {
            throw new RuntimeException("if loop condition badly formed");
        }

        currentEnv.addNode(cmp); // jmp to else block if 0 == cond.
        currentEnv.addNode(new LlJmp(JumpType.EQUAL, ifElse));
        
        // add true branch.
        node.getTrueBlock().accept(this);
        currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL, ifEnd));
        // add false branch.
        currentEnv.addNode(ifElse);
        if (node.getFalseBlock() != null) {
            node.getFalseBlock().accept(this);
        }
        currentEnv.addNode(ifEnd);
        currentEnv.addNode(end);
    }
    
    @Override
    public void visit(IrForStmt node) {
        LlAnnotation start = new LlAnnotation(AnnotationType.FOR,
                "for_" + String.valueOf(forCounter), false, null);
        LlAnnotation end = new LlAnnotation(AnnotationType.FOR,
                "for_" + String.valueOf(forCounter), true, null);
        LlLabel forLoop = new LlLabel("forloop_" + String.valueOf(forCounter));
        LlLabel forDone = new LlLabel("fordone_" + String.valueOf(forCounter));
        
        forCounter++;
        currentEnv.addNode(start);
        
        // init the loop counter.
        LlTempLoc counterLoc = new LlTempLoc(node.getCounterSymbol());
        LlAssign a;
        IrExpression startExpr = node.getStartValue();
        acceptExpr(startExpr);
        if (currentLoc != null) {
            a = new LlAssign(counterLoc, currentLoc, null, true);
        } else if (currentLit != null) {
            a = new LlAssign(counterLoc, currentLit, null, true);
        } else {
            throw new RuntimeException("for loop counter badly formed");
        }
        currentEnv.addNode(a);
        
        // update this visitor's current breakpoint and continuepoint.
        LlLabel oldBreak = breakPoint;
        LlLabel oldContinue = continuePoint;
        breakPoint = forDone;
        continuePoint = forLoop;
        
        // inside the loop body. loop condition checking.
        currentEnv.addNode(forLoop);
        IrExpression stopExpr = node.getStopValue();
        LlCmp cmp;
        acceptExpr(stopExpr); // eval end condition.
        if (currentLoc != null) {
            cmp = new LlCmp(counterLoc, currentLoc);
        } else if (currentLit != null) {
            cmp = new LlCmp(counterLoc, currentLit);
        } else {
            throw new RuntimeException("for loop condition badly formed");
        }
        
        // the loop exits iff loc == stopExpr.
        currentEnv.addNode(cmp);
        currentEnv.addNode(new LlJmp(JumpType.EQUAL, forDone));
        
        // walk the for loop's body.
        node.getBlock().accept(this);

        // increment the counter; repeat the loop.
        LlBinaryAssign increment = new LlBinaryAssign(counterLoc, counterLoc, new LlIntLiteral(1),
                Type.INT, IrBinOperator.PLUS);
        currentEnv.addNode(increment);
        currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL, forLoop));
        
        // out of the loop.
        currentEnv.addNode(forDone);
        currentEnv.addNode(end);
        
        breakPoint = oldBreak;
        continuePoint = oldContinue;
    }

    @Override
    public void visit(IrWhileStmt node) {
        LlAnnotation start = new LlAnnotation(AnnotationType.WHILE,
                "while_" + String.valueOf(whileCounter), false, null);
        LlAnnotation end = new LlAnnotation(AnnotationType.WHILE,
                "while_" + String.valueOf(whileCounter), true, null);
        LlLabel whileLoop = new LlLabel("forloop_" + String.valueOf(whileCounter));
        LlLabel whileDone = new LlLabel("fordone_" + String.valueOf(whileCounter));
        
        whileCounter++;
        currentEnv.addNode(start);
        
        // update this visitor's current breakpoint and continuepoint.
        LlLabel oldBreak = breakPoint;
        LlLabel oldContinue = continuePoint;
        breakPoint = whileDone;
        continuePoint = whileLoop;
        
        // inside the loop.
        currentEnv.addNode(whileLoop);
        
        // check the while condition.
        IrExpression whileExpr = node.getCondition();
        LlCmp cmp;
        acceptExpr(whileExpr);
        if (currentLoc != null) {
            cmp = new LlCmp(currentLoc);
        } else if (currentLit != null) {
            cmp = new LlCmp(currentLit);
        } else {
            throw new RuntimeException("for loop condition badly formed");
        }
        
        currentEnv.addNode(cmp); // jmp outside the loop if 0 == cond.
        currentEnv.addNode(new LlJmp(JumpType.EQUAL, whileDone));
        
        // walk the while loop's body.
        node.getBlock().accept(this);
        currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL, whileLoop));
        
        // outside the loop.
        currentEnv.addNode(whileDone);
        currentEnv.addNode(end);
        
        breakPoint = oldBreak;
        continuePoint = oldContinue;
    }

    @Override
    public void visit(IrBreakStmt node) {
        currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL, breakPoint));
    }    
    
    @Override
    public void visit(IrContinueStmt node) {
        currentEnv.addNode(new LlJmp(JumpType.UNCONDITIONAL, continuePoint));
    }
    
    // Unused visitor methods.
    
    @Override
    public void visit(IrParameterDecl irParameterDecl) {
        // no need to visit.
    }

    @Override
    public void visit(IrStringLiteral irStringLiteral) {
        // no need to visit.
    }

    @Override
    public void visit(IrType irType) {
        // no need to visit.
    }

}
