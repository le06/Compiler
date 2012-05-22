package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLJump.JumpType;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLLocation;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class IrForStmt extends IrStatement {
    public IrForStmt(IrIdentifier counter, IrExpression start_value,
            IrExpression stop_value, IrBlock block) {
        this.counter = counter;
        this.start_value = start_value;
        this.stop_value = stop_value;
        this.for_block = block;
    }

    private IrIdentifier counter;
    private String counter_symbol;
    private IrExpression start_value;
    private IrExpression stop_value;
    private IrBlock for_block;
    
    private int counter_bp_offset;
    
    public IrIdentifier getCounter() {
        return counter;
    }

    public IrExpression getStartValue() {
        return start_value;
    }

    public IrExpression getStopValue() {
        return stop_value;
    }

    public IrBlock getBlock() {
        return for_block;
    }
    
    public String getCounterSymbol() {
        return counter_symbol;
    }
    
    public void setCounterSymbol(String symbol) {
        counter_symbol = symbol;
    }
    
    @Override
    public void accept(IrNodeVisitor v) {
        v.visit(this);
    }

    public void setBpOffset(int offset) {
    	counter_bp_offset = offset;
    }
    
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("FOR:\n");
        out.append(counter.toString(s + 1).concat("\n"));
        out.append(start_value.toString(s + 1).concat("\n"));
        out.append(stop_value.toString(s + 1).concat("\n"));
        out.append(for_block.toString(s + 1).concat("\n"));

        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLLabel for_begin = new LLLabel("for_begin");
        LLLabel for_end = new LLLabel("for_end");
        
        
        //LLVarDecl dec = new LLVarDecl(myCounter.getId());
        LLLocation var = (LLLocation)(new LLVarLocation(counter_bp_offset,
                                                        counter.getId(),
                                                        LLExpression.Type.INT));
        
        LLAssign init = new LLAssign(var,
                                    (LLExpression)start_value.getllRep(null, null));
        
        LLAssign incr = new LLAssign(var,
                               (LLExpression)(new LLBinaryOp((LLExpression)var,
                                                          (LLExpression)(new LLIntLiteral(1)),
                                                          IrBinOperator.PLUS,
                                                          LLExpression.Type.INT)));
        
        LLExpression test = new LLBinaryOp((LLExpression)var,
                                         (LLExpression)stop_value.getllRep(null, null),
                                         IrBinOperator.LT,
                                         LLExpression.Type.INT);
        
        LLEnvironment block = (LLEnvironment)for_block.getllRep(for_end, for_begin);
        
        LLJump jump_end = new LLJump(test, false, for_end);
        LLJump jump_begin = new LLJump(JumpType.UNCONDITIONAL, for_begin);
        
        
        LLEnvironment out = new LLEnvironment();
        //out.addNode(dec);
        out.addNode(init);
        out.addNode(for_begin);
        //out.addNode(test);
        out.addNode(jump_end);
        out.addNode(block);
        out.addNode(incr);
        out.addNode(jump_begin);
        out.addNode(for_end);
        
        return out;
    }
}