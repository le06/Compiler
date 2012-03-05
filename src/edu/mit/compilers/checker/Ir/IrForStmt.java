package edu.mit.compilers.checker.Ir;

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
}