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
    //private LlNode ll;

    /*
     * The state of the LL generator as it walks the IR tree.
     */
    private LlProgram program;
    private LlBlock currentBlock; // the current block/environment where instructions should be added.
    private LlBlock returnBlock;  // the control flow 'sink' of each method.
    private ArrayList<LlBlock> currentBlocksInOrder;
    private int blockCounter = 1;

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
    
    private LlLabel breakLabel;
    private LlBlock breakBlock;
    private LlLabel continueLabel;
    private LlBlock continueBlock;
    
    // expects the root node as input.
    public LlGenerator(Ir ir) {
        this.ir = ir;
    }
    

    public ArrayList<LlNode> generateSequence(LlProgram p) {
        ArrayList<LlNode> sequence = new ArrayList<LlNode>();
        
        for (LlGlobalDecl n : p.getGlobalDecls()) {
            sequence.add(n);
        }
        for (LlArrayDecl n : p.getArrayDecls()) {
            sequence.add(n);
        }
        for (LlMethodDecl n : p.getMethods()) {
            sequence.add(n);
            sequence.add(n.getReturnBlock());
            for (LlBlock bb : n.getBlocksInOrder()) {
                sequence.add(bb);
                for (LlNode inst : bb.getInstructions()) {
                    sequence.add(inst);
                }
            }
        }
        LlMethodDecl m = p.getMain();
        sequence.add(m);
        sequence.add(m.getReturnBlock());
        for (LlBlock bb : m.getBlocksInOrder()) {
            sequence.add(bb);
            for (LlNode inst : bb.getInstructions()) {
                sequence.add(inst);
            }
        }
        for (LlStringLiteral n : p.getStringLiterals()) {
            sequence.add(n);
        }
        
        return sequence;
    }
    
    public LlProgram generateLL() {
        ir.accept(this);

        // TODO: debug as necessary.
        for (LlNode n : generateSequence(program)) {
            System.out.println(n);
        }
        
        // TODO: should the return type be tweaked?
        return program;
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
    
    private LlBlock createBlock(String method) {
        LlBlock newBlock = new LlBlock(method, blockCounter);
        blockCounter++;
        return newBlock;
    }
    
    @Override
    public void visit(IrMethodDecl node) {
        returnBlock = null;
        currentBlock = null;
        currentBlocksInOrder = new ArrayList<LlBlock>();
        
        String name = node.getId().getId();
        int num_args = node.getParams().size();
        LlMethodDecl m;
        
        // walk the method contents.
        returnBlock = createBlock(name);
        currentBlock = createBlock(name);
        currentBlocksInOrder.add(currentBlock);
        node.getBlock().accept(this);
        
        // always return at the end of a method.
        LlBlock.pairUp(currentBlock, returnBlock);
        
        // set the method's return type.
        switch (node.getReturnType().myType) {
        case BOOLEAN:
            m = new LlMethodDecl(MethodType.BOOLEAN, name, num_args);
            break;
        case INT:
            m = new LlMethodDecl(MethodType.INT, name, num_args);
            break;
        case VOID:
            m = new LlMethodDecl(MethodType.VOID, name, num_args);
            currentBlock.addInstruction(new LlReturn(new LlIntLiteral(0)));
            break;
        default:
            throw new RuntimeException("Cannot have a method with mixed type");
        }
        
        // add the basic blocks to the method declaration.
        m.setBlocksInOrder(currentBlocksInOrder);
        m.setReturnBlock(returnBlock);
        
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
        
        if (name.equals("main")) {
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
        currentBlock.addInstruction(a);
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
        
        // direct CFG to sink.
        currentBlock.addInstruction(r);
        LlBlock.pairUp(currentBlock, returnBlock);
        // create a new, unconnected block.
        LlBlock nextBlock = createBlock(currentBlock.getMethod());
        currentBlock = nextBlock;
        currentBlocksInOrder.add(nextBlock);
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
        
        // add method call to block.
        currentBlock.addInstruction(mc);
        currentBlock.setCalls(node.getMethodName().getId());
        
        // create a new block.
        LlBlock nextBlock = createBlock(currentBlock.getMethod());
        nextBlock.setReturnsFrom(node.getMethodName().getId());
        
        // pair up pre-method-call and post-method-call blocks.
        LlBlock.pairUp(currentBlock, nextBlock);
        currentBlock = nextBlock;
        currentBlocksInOrder.add(nextBlock);
        
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
        
        currentBlock.addInstruction(co);
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
                    "ss_" + String.valueOf(ssCounter), false, null);
            LlAnnotation end = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss_" + String.valueOf(ssCounter), true, null);
            LlLabel ss = new LlLabel("ss_" + String.valueOf(ssCounter));
            LlLabel ssEnd = new LlLabel("ssend_" + String.valueOf(ssCounter));
            
            ssCounter++;
            currentBlock.addInstruction(start);
            
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
            currentBlock.addInstruction(cmp);
            currentBlock.addInstruction(new LlJmp(JumpType.EQUAL,ss));
            
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
            currentBlock.addInstruction(assign);
            currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL,ssEnd));
            
            // if short-circuit, result = 0;
            currentBlock.addInstruction(ss);
            currentBlock.addInstruction(new LlAssign(result, new LlIntLiteral(0), Type.BOOLEAN, false));
            currentBlock.addInstruction(ssEnd);
            currentBlock.addInstruction(end);
            
            // annotate beginning and end of short-circuit code.
            String annotation = result.getSymbol() + "=" + leftSymbol + "&&" + rightSymbol;
            start.setAnnotation(annotation);
            end.setAnnotation(annotation);
            
        } else if (op == IrBinOperator.OR) {
            LlAnnotation start = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss_" + String.valueOf(ssCounter), false, null);
            LlAnnotation end = new LlAnnotation(AnnotationType.COND_OP_ASSIGNMENT,
                    "ss_" + String.valueOf(ssCounter), true, null);
            LlLabel ss = new LlLabel("ss_" + String.valueOf(ssCounter));
            LlLabel ssEnd = new LlLabel("ssend_" + String.valueOf(ssCounter));
            
            ssCounter++;
            currentBlock.addInstruction(start);
            
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
            currentBlock.addInstruction(cmp); // cmp 0, left
            currentBlock.addInstruction(new LlJmp(JumpType.NOT_EQUAL,ss)); // 0 != 1
            
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
            currentBlock.addInstruction(assign);
            currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL,ssEnd));
            
            // if short-circuit, result = 1;
            currentBlock.addInstruction(ss);
            currentBlock.addInstruction(new LlAssign(result, new LlIntLiteral(1), Type.BOOLEAN, false));
            currentBlock.addInstruction(ssEnd);
            currentBlock.addInstruction(end);
            
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
            
            currentBlock.addInstruction(a);    
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

        currentBlock.addInstruction(a);
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

        currentBlock.addInstruction(a);
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

        currentBlock.addInstruction(a);
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

        currentBlock.addInstruction(a);
    }
    
    // Control flow.
    
    @Override
    public void visit(IrIfStmt node) {
        LlAnnotation start = new LlAnnotation(AnnotationType.IF,
                "if_" + String.valueOf(ifCounter), false, null);
        LlAnnotation end = new LlAnnotation(AnnotationType.IF,
                "if_" + String.valueOf(ifCounter), true, null);
        LlLabel ifElse = new LlLabel("ifelse_" + String.valueOf(ifCounter));
        LlLabel ifEnd = new LlLabel("ifend_" + String.valueOf(ifCounter));
        
        ifCounter++;
        currentBlock.addInstruction(start);
        
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

        currentBlock.addInstruction(cmp); // jmp to else block if 0 == cond.
        currentBlock.addInstruction(new LlJmp(JumpType.EQUAL, ifElse));
        
        // define blocks.
        LlBlock preBlock = currentBlock;
        LlBlock postBlock = createBlock(currentBlock.getMethod());
        
        // walk true branch.
        LlBlock trueBlock = createBlock(currentBlock.getMethod());
        currentBlock = trueBlock;
        currentBlocksInOrder.add(trueBlock);
        
        node.getTrueBlock().accept(this);
        currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL, ifEnd));
        
        // connect true branch.
        LlBlock.pairUp(preBlock, trueBlock);
        LlBlock.pairUp(currentBlock, postBlock);
        
        // walk false branch, if necessary.
        if (node.getFalseBlock() != null) {
            LlBlock falseBlock = createBlock(currentBlock.getMethod());
            currentBlock = falseBlock;
            currentBlocksInOrder.add(falseBlock);
            
            currentBlock.addInstruction(ifElse);
            node.getFalseBlock().accept(this);
            
            // connect false branch.
            LlBlock.pairUp(preBlock, falseBlock);
            LlBlock.pairUp(currentBlock, postBlock);
            
            currentBlock = postBlock;
            currentBlocksInOrder.add(postBlock);
            
            currentBlock.addInstruction(ifEnd);
            currentBlock.addInstruction(end);
        } else {
            // connect false branch.
            LlBlock.pairUp(preBlock, postBlock);
            
            currentBlock = postBlock;
            currentBlocksInOrder.add(postBlock);
            
            currentBlock.addInstruction(ifElse);
            currentBlock.addInstruction(ifEnd);
            currentBlock.addInstruction(end);
        }
        
    }
    
    @Override
    public void visit(IrForStmt node) {
        LlAnnotation start = new LlAnnotation(AnnotationType.FOR,
                "for_" + String.valueOf(forCounter), false, null);
        LlAnnotation end = new LlAnnotation(AnnotationType.FOR,
                "for_" + String.valueOf(forCounter), true, null);
        LlLabel forLoop = new LlLabel("forloop_" + String.valueOf(forCounter));
        LlLabel forIncr = new LlLabel("forincr_" + String.valueOf(forCounter));
        LlLabel forDone = new LlLabel("fordone_" + String.valueOf(forCounter));
        
        forCounter++;
        currentBlock.addInstruction(start);
        
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
        currentBlock.addInstruction(a);
        
        // init blocks.
        LlBlock preBlock = currentBlock;
        LlBlock forCondition = createBlock(currentBlock.getMethod());
        LlBlock forBody = createBlock(currentBlock.getMethod());
        LlBlock forEndOfBody = createBlock(currentBlock.getMethod());
        LlBlock postBlock = createBlock(currentBlock.getMethod());
        
        // update this visitor's current break and continue points.
        LlLabel oldBreakLabel = breakLabel;
        LlBlock oldBreakBlock = breakBlock;
        LlLabel oldContinueLabel = continueLabel;
        LlBlock oldContinueBlock = continueBlock;
        
        breakLabel = forDone;
        breakBlock = postBlock;
        continueLabel = forIncr;
        continueBlock = forEndOfBody;
        
        // loop condition checking is its own basic block.
        LlBlock.pairUp(preBlock, forCondition);
        currentBlock = forCondition;
        currentBlocksInOrder.add(forCondition);
        
        currentBlock.addInstruction(forLoop);
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
        currentBlock.addInstruction(cmp);
        currentBlock.addInstruction(new LlJmp(JumpType.EQUAL, forDone));
        LlBlock.pairUp(currentBlock, postBlock);
        LlBlock.pairUp(currentBlock, forBody);
        
        // walk the for loop's body.
        currentBlock = forBody;
        currentBlocksInOrder.add(forBody);        
        node.getBlock().accept(this);

        // increment the counter; repeat the loop.
        LlBlock.pairUp(currentBlock, forEndOfBody);        
        currentBlock = forEndOfBody;
        currentBlocksInOrder.add(forEndOfBody);
        LlBinaryAssign increment = new LlBinaryAssign(counterLoc, counterLoc, new LlIntLiteral(1),
                Type.INT, IrBinOperator.PLUS);
        currentBlock.addInstruction(forIncr);
        currentBlock.addInstruction(increment);
        currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL, forLoop));
        LlBlock.pairUp(currentBlock, forCondition);
        
        // out of the loop.
        currentBlock = postBlock;
        currentBlocksInOrder.add(postBlock);
        
        currentBlock.addInstruction(forDone);
        currentBlock.addInstruction(end);
        
        // revert break and continue points.
        breakLabel = oldBreakLabel;
        breakBlock = oldBreakBlock;
        continueLabel = oldContinueLabel;
        continueBlock = oldContinueBlock;
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
        currentBlock.addInstruction(start);
        
        // init blocks.
        LlBlock preBlock = currentBlock;
        LlBlock whileCondition = createBlock(currentBlock.getMethod());
        LlBlock whileBody = createBlock(currentBlock.getMethod());
        LlBlock postBlock = createBlock(currentBlock.getMethod());
        
        // update this visitor's current break and continue points.
        LlLabel oldBreakLabel = breakLabel;
        LlBlock oldBreakBlock = breakBlock;
        LlLabel oldContinueLabel = continueLabel;
        LlBlock oldContinueBlock = continueBlock;
        
        breakLabel = whileDone;
        breakBlock = postBlock;
        continueLabel = whileLoop;
        continueBlock = whileCondition;
        
        // inside the loop.
        LlBlock.pairUp(preBlock, whileCondition);
        currentBlock = whileCondition;
        currentBlocksInOrder.add(whileCondition);
        currentBlock.addInstruction(whileLoop);
        
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
        
        currentBlock.addInstruction(cmp); // jmp outside the loop if 0 == cond.
        currentBlock.addInstruction(new LlJmp(JumpType.EQUAL, whileDone));
        LlBlock.pairUp(currentBlock, whileBody);
        LlBlock.pairUp(currentBlock, postBlock);
        
        // walk the while loop's body.
        currentBlock = whileBody;
        currentBlocksInOrder.add(whileBody);
        
        node.getBlock().accept(this);
        currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL, whileLoop));
        LlBlock.pairUp(currentBlock, whileCondition);
        
        // outside the loop.
        currentBlock = postBlock;
        currentBlocksInOrder.add(postBlock);
        currentBlock.addInstruction(whileDone);
        currentBlock.addInstruction(end);
        
        // revert break and continue points.
        breakLabel = oldBreakLabel;
        breakBlock = oldBreakBlock;
        continueLabel = oldContinueLabel;
        continueBlock = oldContinueBlock;
    }
    
    @Override
    public void visit(IrBreakStmt node) {
        currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL, breakLabel));
        LlBlock.pairUp(currentBlock, breakBlock);

        LlBlock nextBlock = createBlock(currentBlock.getMethod());
        currentBlock = nextBlock;
        currentBlocksInOrder.add(nextBlock);
    }
    
    @Override
    public void visit(IrContinueStmt node) {
        currentBlock.addInstruction(new LlJmp(JumpType.UNCONDITIONAL, continueLabel));
        LlBlock.pairUp(currentBlock, continueBlock);

        LlBlock nextBlock = createBlock(currentBlock.getMethod());
        currentBlock = nextBlock;
        currentBlocksInOrder.add(nextBlock);
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
