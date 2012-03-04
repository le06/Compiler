package edu.mit.compilers.checker.Ir;
// import ArrayList(?)

public abstract class Ir {
    private Ir left;
    private Ir right;
    private int line;
    
    public Ir getLeft() {
        return left;
    }
    
    public Ir getRight() {
        return right;
    }
    
    // abstract walk()
}

// since Program is a token, a generated Ir should have a IrClassDecl node
// as its root.
public class IrClassDecl extends Ir {
    // order matters! need abstract members.
    private ArrayList<IrMemberDecl> members;
}

public abstract class IrMemberDecl extends Ir {}

    public abstract class IrFieldDecl extends IrMemberDecl {}
    
        public class IrGlobalDecl extends IrFieldDecl {
            private IrVarDecl var;
        }
        
        public class IrArrayDecl extends IrFieldDecl {
            private IrType type;
            private IrIdentifier id;
            private IrIntLiteral array_size;
        }
        
    public class IrMethodDecl extends IrMemberDecl {
        private IrReturnType return_type;
        private IrIdentifier id;
        private ArrayList<IrVarDecl> args;
        private IrBlock block;
    }

public class IrVarDecl extends Ir {
    private IrType type;
    private IrIdentifier id;
}

public class IrBlock extends Ir {
    // ordering between vars and stmts enforced at parse-time.
    private ArrayList<IrVarDecl> var_decls;
    private ArrayList<IrStatement> statements;
}



public abstract class IrStatement extends Ir {}

    public class IrAssignStmt extends IrStatement {
        private IrLocation lhs;
        private IrExpression rhs;
    }
    
    public class IrPlusAssignStmt extends IrStatement {
        private IrLocation lhs;
        private IrExpression rhs;
    }

    public class IrMinusAssignStmt extends IrStatement {
        private IrLocation lhs;
        private IrExpression rhs;
    }
    
    public class IrContinueStmt extends IrStatement {}
    
    public class IrBreakStmt extends IrStatement {}
    
    public class IrReturnStmt extends IrStatement {
        private IrExpression return_expr;
    }
    
    public class IrWhileSmt extends IrStatement {
        private IrExpression condition;
        private IrBlock block;
    }
    
    public class IrForStmt extends IrStatement {
        private IrIdentifier counter;
        private IrExpression start_value;
        private IrExpression stop_value;
        private IrBlock block;
    }
    
    public class IrIfStmt extends IrStatement {
        private IfExpression condition;
        private IrBlock true_block;
        private IrBlock false_block;
    }
    
    public class IrBlockStmt extends IrStatement {
        private IrBlock block;
    }
    
    public abstract class IrInvokeStmt extends IrStatement {}
    
        public class IrMethodCallStmt extends IrInvokeStmt {
            private IrIdentifier method_name;
            private ArrayList<IrExpression> args;
        }
        
        public class IrCalloutStmt extends IrInvokeStmt {
            private IrStringLiteral function_name;
            private ArrayList<IrCalloutArg> args;
        }

public abstract class IrCalloutArg extends Ir {}

    public class IrStringArg extends IrCalloutArg {
        private IrStringLiteral arg;
    }
    
    public class IrExprArg extends IrCalloutArg {
        private IrExpression arg;
    }



public abstract class IrLocation extends Ir {}

    public class IrVarLocation extends IrLocation {
        private IrIdentifier id;
    }

    public class IrArrayLocation extends IrLocation {
        private IrIdentifier id;
        private IrExpression index;
    }

public abstract class IrExpression extends Ir {}

    public class IrLocationExpr extends IrExpression {
        private IrLocation location;
    }
    
    public abstract class IrCallExpr extends IrExpression {}
    
        public class IrMethodCallExpr extends IrCallExpr {
            private IrMethodCallStmt call;
        }
        
        public class IrCalloutExpr extends IrCallExpr {
            private IrCalloutStmt call;
        }
        
    public class IrLiteralExpr extends IrExpression {
        private IrLiteral literal;
    }
    
    public class IrBinopExpr extends IrExpression {
        IrBinOperator operator;
        IrExpression lhs;
        IrExpression rhs;
    }
    
    public class IrUnopExpr extends IrExpression {
        IrUnaryOperator operator;
        IrExpression expr;
    }



// enum type? string type? child nodes?
public class IrBinOperator extends Ir {
    int operator;
}

public class IrUnaryOperator extends Ir {
    int operator;
}

// same question.
public class IrType extends Ir {
    int type;
}

public class IrReturnType extends Ir {
    int type;
}

public class IrIdentifier extends Ir {
    String id;
}

public abstract class IrLiteral extends Ir {}

    public class IrIntLiteral extends IrLiteral {
        int literal;
    }
    
    public class IrCharLiteral extends IrLiteral {
        char literal;
    }
    
    public class IrBoolLiteral extends IrLiteral {
        boolean literal;
    }

public class IrStringLiteral extends Ir {
    String literal;
}
