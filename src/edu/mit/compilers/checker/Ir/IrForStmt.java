package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llIntLiteral;
import edu.mit.compilers.codegen.ll.llJump;
import edu.mit.compilers.codegen.ll.llJump.JumpType;
import edu.mit.compilers.codegen.ll.llAssign;
import edu.mit.compilers.codegen.ll.llBinOp;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llLocation;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llVarAccess;
import edu.mit.compilers.codegen.ll.llVarDec;

public class IrForStmt extends IrStatement {
    public IrForStmt(IrIdentifier counter, IrExpression start_value,
            IrExpression stop_value, IrBlock block) {
        myCounter = counter;
        myStart_value = start_value;
        myStop_value = stop_value;
        myBlock = block;
    }

    private IrIdentifier myCounter;
    private IrExpression myStart_value;
    private IrExpression myStop_value;
    private IrBlock myBlock;

    public IrIdentifier getCounter() {
        return myCounter;
    }

    public IrExpression getStartValue() {
        return myStart_value;
    }

    public IrExpression getStopValue() {
        return myStop_value;
    }

    public IrBlock getBlock() {
        return myBlock;
    }

    @Override
    public void accept(IrNodeVisitor v) {
        v.visit(this);
    }

    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("FOR:\n");
        out.append(myCounter.toString(s + 1).concat("\n"));
        out.append(myStart_value.toString(s + 1).concat("\n"));
        out.append(myStop_value.toString(s + 1).concat("\n"));
        out.append(myBlock.toString(s + 1).concat("\n"));

        return out.toString();
    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        llLabel for_begin = new llLabel("for_begin");
        llLabel for_end = new llLabel("for_end");
        
        llJump jump_end = new llJump(JumpType.EQUAL, for_end);
        llJump jump_begin = new llJump(JumpType.UNCONDITIONAL, for_begin);
        
        llVarDec dec = new llVarDec(myCounter.getId());
        llLocation var = (llLocation)(new llVarAccess(myCounter.getId()));
        
        llAssign init = new llAssign(var,
                                    (llExpression)myStart_value.getllRep(null, null));
        
        llAssign incr = new llAssign(var,
                               (llExpression)(new llBinOp((llExpression)var,
                                                          (llExpression)(new llIntLiteral(1)),
                                                          IrBinOperator.PLUS)));
        
        llExpression test = new llBinOp((llExpression)var,
                                         (llExpression)myStop_value.getllRep(null, null),
                                         IrBinOperator.LT);
        
        llEnvironment block = (llEnvironment)myBlock.getllRep(for_end, for_begin);
        
        
        llEnvironment out = new llEnvironment();
        out.addNode(dec);
        out.addNode(init);
        out.addNode(for_begin);
        out.addNode(test);
        out.addNode(jump_end);
        out.addNode(block);
        out.addNode(incr);
        out.addNode(jump_begin);
        out.addNode(for_end);
        
        return out;
    }
}