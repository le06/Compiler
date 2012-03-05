package edu.mit.compilers.checker.Ir;

public class IrCharLiteral extends Ir implements IrExpression {
    public IrCharLiteral(char value) {
        literal = value;
    }
    
	private char literal;
	
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
	public IrType getExprType(IrNodeChecker c) {
		return new IrType(IrType.Type.INT); // chars are converted to ints.
	}

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// no need to visit; bad chars should be caught by the scanner.
	}
}