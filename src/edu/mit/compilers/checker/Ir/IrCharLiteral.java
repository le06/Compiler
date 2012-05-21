package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.SemanticChecker;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrCharLiteral extends Ir implements IrExpression {
    public IrCharLiteral(char value) {
        literal = value;
    }
    
	private char literal;

	public char getLiteral() {
	    return literal;
	}
	
	public static IrCharLiteral fromString(String inString) {
	    if (inString.length() == 3) {
	        return new IrCharLiteral(inString.charAt(1));
	    } else if (inString.length() == 4) {
	        switch (inString.charAt(2)) {
	        case 'n':
	            return new IrCharLiteral('\n');
	        case 't':
                return new IrCharLiteral('\t');
	        case '"':
                return new IrCharLiteral('\"');
	        case '\\':
                return new IrCharLiteral('\\');
	        case '\'':
	            return new IrCharLiteral('\'');
            default:
                throw new IllegalArgumentException("Illegal escape character in string.");    
	        }
	        
	    } else {
	        throw new IllegalArgumentException("Given String is not a char literal.");
	    }
	}

	@Override
	public IrType getExprType(SemanticChecker c) {
		return new IrType(IrType.Type.INT); // chars are converted to ints.
	}

	@Override
	public void accept(IrNodeVisitor v) {
	    v.visit(this);
		// TODO Auto-generated method stub
	}
	
	public String toString() {
	    return Character.toString(literal);
	}
	
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return new LLIntLiteral((long)literal);
    }
}