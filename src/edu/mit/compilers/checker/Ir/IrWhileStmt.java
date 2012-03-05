package edu.mit.compilers.checker.Ir;

public class IrWhileStmt extends IrStatement {
    public IrWhileStmt(IrExpression test, IrBlock true_block) {
        condition = test;
        block = true_block;
    }

    private IrExpression condition;
    private IrBlock block;

    public IrExpression getCondition() {
        return condition;
    }

    public IrBlock getBlock() {
        return block;
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
        out.append("WHILE:\n");
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(condition.toString().concat("\n"));

        out.append(block.toString(s+1).concat("\n"));

        return out.toString();
    }
}