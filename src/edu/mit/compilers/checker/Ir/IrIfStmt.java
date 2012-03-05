package edu.mit.compilers.checker.Ir;

public class IrIfStmt extends IrStatement {
    public IrIfStmt(IrExpression test, IrBlock if_block, IrBlock else_block) {
        condition = test;
        true_block = if_block;
        false_block = else_block;
    }

    private IrExpression condition;
    private IrBlock true_block;
    private IrBlock false_block;

    public IrExpression getCondition() {
        return condition;
    }

    public IrBlock getTrueBlock() {
        return true_block;
    }

    public IrBlock getFalseBlock() {
        return false_block;
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
        out.append("IF:\n");
        out.append(condition.toString(s + 1).concat("\n"));
        out.append(true_block.toString(s+1).concat("\n"));
        if (false_block != null) {
            out.append(false_block.toString(s+1).concat("\n"));
        }

        return out.toString();
    }
}