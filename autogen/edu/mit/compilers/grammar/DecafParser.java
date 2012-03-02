// $ANTLR 2.7.7 (2006-11-01): "parser.g" -> "DecafParser.java"$

package edu.mit.compilers.grammar;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class DecafParser extends antlr.LLkParser       implements DecafParserTokenTypes
 {

  // Do our own reporting of errors so the parser can return a non-zero status
  // if any errors are detected.
  /** Reports if any errors were reported during parse. */
  private boolean error;

  @Override
  public void reportError (RecognitionException ex) {
    // Print the error via some kind of error reporting mechanism.
    System.err.println(ex.toString());
    error = true;
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    System.err.println(s);
    error = true;
  }
  public boolean getError () {
    return error;
  }

  public void outputError () {
    System.err.println("Error while parsing!");
  }

  // Selectively turns on debug mode.

  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws TokenStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws TokenStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }

protected DecafParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DecafParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected DecafParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DecafParser(TokenStream lexer) {
  this(lexer,3);
}

public DecafParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		traceIn("program");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST program_AST = null;
			
			try {      // for error handling
				AST tmp1_AST = null;
				tmp1_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp1_AST);
				match(TK_class);
				AST tmp2_AST = null;
				tmp2_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp2_AST);
				match(TK_Program);
				match(LCURLY);
				{
				_loop3:
				do {
					if ((LA(1)==TK_boolean||LA(1)==TK_int) && (LA(2)==ID) && (LA(3)==LSQUARE||LA(3)==COMMA||LA(3)==SEMI)) {
						field_dec();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop3;
					}
					
				} while (true);
				}
				{
				_loop5:
				do {
					if ((LA(1)==TK_boolean||LA(1)==TK_int||LA(1)==TK_void)) {
						method_dec();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop5;
					}
					
				} while (true);
				}
				match(RCURLY);
				match(Token.EOF_TYPE);
				program_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			}
			returnAST = program_AST;
		} finally { // debugging
			traceOut("program");
		}
	}
	
	public final void field_dec() throws RecognitionException, TokenStreamException {
		
		traceIn("field_dec");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST field_dec_AST = null;
			
			try {      // for error handling
				type();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if ((LA(1)==ID) && (LA(2)==COMMA||LA(2)==SEMI)) {
					AST tmp6_AST = null;
					tmp6_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp6_AST);
					match(ID);
				}
				else if ((LA(1)==ID) && (LA(2)==LSQUARE)) {
					AST tmp7_AST = null;
					tmp7_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp7_AST);
					match(ID);
					match(LSQUARE);
					AST tmp9_AST = null;
					tmp9_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp9_AST);
					match(INTLITERAL);
					match(RSQUARE);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				{
				_loop10:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						{
						if ((LA(1)==ID) && (LA(2)==COMMA||LA(2)==SEMI)) {
							AST tmp12_AST = null;
							tmp12_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp12_AST);
							match(ID);
						}
						else if ((LA(1)==ID) && (LA(2)==LSQUARE)) {
							AST tmp13_AST = null;
							tmp13_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp13_AST);
							match(ID);
							match(LSQUARE);
							AST tmp15_AST = null;
							tmp15_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp15_AST);
							match(INTLITERAL);
							match(RSQUARE);
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						
						}
					}
					else {
						break _loop10;
					}
					
				} while (true);
				}
				match(SEMI);
				field_dec_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			}
			returnAST = field_dec_AST;
		} finally { // debugging
			traceOut("field_dec");
		}
	}
	
	public final void method_dec() throws RecognitionException, TokenStreamException {
		
		traceIn("method_dec");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_dec_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case TK_boolean:
				case TK_int:
				{
					type();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case TK_void:
				{
					AST tmp18_AST = null;
					tmp18_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp18_AST);
					match(TK_void);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp19_AST);
				match(ID);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case TK_boolean:
				case TK_int:
				{
					{
					type();
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp21_AST = null;
					tmp21_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp21_AST);
					match(ID);
					}
					{
					_loop16:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							type();
							astFactory.addASTChild(currentAST, returnAST);
							AST tmp23_AST = null;
							tmp23_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp23_AST);
							match(ID);
						}
						else {
							break _loop16;
						}
						
					} while (true);
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				method_dec_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			}
			returnAST = method_dec_AST;
		} finally { // debugging
			traceOut("method_dec");
		}
	}
	
	public final void type() throws RecognitionException, TokenStreamException {
		
		traceIn("type");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST type_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case TK_int:
				{
					AST tmp25_AST = null;
					tmp25_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp25_AST);
					match(TK_int);
					type_AST = (AST)currentAST.root;
					break;
				}
				case TK_boolean:
				{
					AST tmp26_AST = null;
					tmp26_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp26_AST);
					match(TK_boolean);
					type_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			}
			returnAST = type_AST;
		} finally { // debugging
			traceOut("type");
		}
	}
	
	public final void block() throws RecognitionException, TokenStreamException {
		
		traceIn("block");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST block_AST = null;
			
			try {      // for error handling
				match(LCURLY);
				{
				_loop19:
				do {
					if ((LA(1)==TK_boolean||LA(1)==TK_int)) {
						var_decl();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop19;
					}
					
				} while (true);
				}
				{
				_loop21:
				do {
					if ((_tokenSet_3.member(LA(1)))) {
						statement();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop21;
					}
					
				} while (true);
				}
				match(RCURLY);
				block_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = block_AST;
		} finally { // debugging
			traceOut("block");
		}
	}
	
	public final void var_decl() throws RecognitionException, TokenStreamException {
		
		traceIn("var_decl");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST var_decl_AST = null;
			
			try {      // for error handling
				type();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp29_AST = null;
				tmp29_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp29_AST);
				match(ID);
				{
				_loop24:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						AST tmp31_AST = null;
						tmp31_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp31_AST);
						match(ID);
					}
					else {
						break _loop24;
					}
					
				} while (true);
				}
				match(SEMI);
				var_decl_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			}
			returnAST = var_decl_AST;
		} finally { // debugging
			traceOut("var_decl");
		}
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		traceIn("statement");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST statement_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case TK_if:
				{
					AST tmp33_AST = null;
					tmp33_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp33_AST);
					match(TK_if);
					{
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					}
					block();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case TK_else:
					{
						AST tmp34_AST = null;
						tmp34_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp34_AST);
						match(TK_else);
						block();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case TK_break:
					case TK_callout:
					case TK_continue:
					case TK_for:
					case TK_if:
					case TK_return:
					case TK_while:
					case LCURLY:
					case RCURLY:
					case ID:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_for:
				{
					AST tmp35_AST = null;
					tmp35_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp35_AST);
					match(TK_for);
					{
					AST tmp36_AST = null;
					tmp36_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp36_AST);
					match(ID);
					AST tmp37_AST = null;
					tmp37_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp37_AST);
					match(ASSIGN);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					match(COMMA);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					}
					block();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_while:
				{
					AST tmp39_AST = null;
					tmp39_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp39_AST);
					match(TK_while);
					{
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					}
					block();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_return:
				{
					AST tmp40_AST = null;
					tmp40_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp40_AST);
					match(TK_return);
					{
					switch ( LA(1)) {
					case TK_callout:
					case LPAREN:
					case NOT:
					case MINUS:
					case ID:
					case INTLITERAL:
					case CHAR:
					case BOOLEANLITERAL:
					{
						expr();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case SEMI:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(SEMI);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_break:
				{
					AST tmp42_AST = null;
					tmp42_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp42_AST);
					match(TK_break);
					match(SEMI);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_continue:
				{
					AST tmp44_AST = null;
					tmp44_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp44_AST);
					match(TK_continue);
					match(SEMI);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case LCURLY:
				{
					block();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				default:
					if ((LA(1)==ID) && (_tokenSet_6.member(LA(2)))) {
						assignment();
						astFactory.addASTChild(currentAST, returnAST);
						match(SEMI);
						statement_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==TK_callout||LA(1)==ID) && (LA(2)==LPAREN)) {
						method_call();
						astFactory.addASTChild(currentAST, returnAST);
						match(SEMI);
						statement_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			}
			returnAST = statement_AST;
		} finally { // debugging
			traceOut("statement");
		}
	}
	
	public final void assignment() throws RecognitionException, TokenStreamException {
		
		traceIn("assignment");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST assignment_AST = null;
			AST left_AST = null;
			AST op_AST = null;
			AST right_AST = null;
			
			try {      // for error handling
				location();
				left_AST = (AST)returnAST;
				assign_op();
				op_AST = (AST)returnAST;
				expr();
				right_AST = (AST)returnAST;
				assignment_AST = (AST)currentAST.root;
				assignment_AST = (AST)astFactory.make( (new ASTArray(3)).add(op_AST).add(left_AST).add(right_AST));
				currentAST.root = assignment_AST;
				currentAST.child = assignment_AST!=null &&assignment_AST.getFirstChild()!=null ?
					assignment_AST.getFirstChild() : assignment_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			}
			returnAST = assignment_AST;
		} finally { // debugging
			traceOut("assignment");
		}
	}
	
	public final void method_call() throws RecognitionException, TokenStreamException {
		
		traceIn("method_call");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_call_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case ID:
				{
					method_name();
					astFactory.addASTChild(currentAST, returnAST);
					match(LPAREN);
					{
					switch ( LA(1)) {
					case TK_callout:
					case LPAREN:
					case NOT:
					case MINUS:
					case ID:
					case INTLITERAL:
					case CHAR:
					case BOOLEANLITERAL:
					{
						expr();
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop37:
						do {
							if ((LA(1)==COMMA)) {
								match(COMMA);
								expr();
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop37;
							}
							
						} while (true);
						}
						break;
					}
					case RPAREN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(RPAREN);
					method_call_AST = (AST)currentAST.root;
					break;
				}
				case TK_callout:
				{
					AST tmp51_AST = null;
					tmp51_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp51_AST);
					match(TK_callout);
					match(LPAREN);
					AST tmp53_AST = null;
					tmp53_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp53_AST);
					match(STRING);
					{
					_loop39:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							callout_arg();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop39;
						}
						
					} while (true);
					}
					match(RPAREN);
					method_call_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = method_call_AST;
		} finally { // debugging
			traceOut("method_call");
		}
	}
	
	public final void expr() throws RecognitionException, TokenStreamException {
		
		traceIn("expr");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_AST = null;
			
			try {      // for error handling
				expr_l0();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if ((LA(1)==OR) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					or_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_AST;
		} finally { // debugging
			traceOut("expr");
		}
	}
	
	public final void location() throws RecognitionException, TokenStreamException {
		
		traceIn("location");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST location_AST = null;
			
			try {      // for error handling
				if ((LA(1)==ID) && (_tokenSet_14.member(LA(2)))) {
					AST tmp56_AST = null;
					tmp56_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp56_AST);
					match(ID);
					location_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==LSQUARE)) {
					AST tmp57_AST = null;
					tmp57_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp57_AST);
					match(ID);
					match(LSQUARE);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					match(RSQUARE);
					location_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			}
			returnAST = location_AST;
		} finally { // debugging
			traceOut("location");
		}
	}
	
	public final void assign_op() throws RecognitionException, TokenStreamException {
		
		traceIn("assign_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST assign_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case ASSIGN:
				{
					AST tmp60_AST = null;
					tmp60_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp60_AST);
					match(ASSIGN);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				case INC_ASSIGN:
				{
					AST tmp61_AST = null;
					tmp61_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp61_AST);
					match(INC_ASSIGN);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				case MINUS_ASSIGN:
				{
					AST tmp62_AST = null;
					tmp62_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp62_AST);
					match(MINUS_ASSIGN);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = assign_op_AST;
		} finally { // debugging
			traceOut("assign_op");
		}
	}
	
	public final void method_name() throws RecognitionException, TokenStreamException {
		
		traceIn("method_name");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_name_AST = null;
			
			try {      // for error handling
				AST tmp63_AST = null;
				tmp63_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp63_AST);
				match(ID);
				method_name_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			}
			returnAST = method_name_AST;
		} finally { // debugging
			traceOut("method_name");
		}
	}
	
	public final void callout_arg() throws RecognitionException, TokenStreamException {
		
		traceIn("callout_arg");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST callout_arg_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case TK_callout:
				case LPAREN:
				case NOT:
				case MINUS:
				case ID:
				case INTLITERAL:
				case CHAR:
				case BOOLEANLITERAL:
				{
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					callout_arg_AST = (AST)currentAST.root;
					break;
				}
				case STRING:
				{
					AST tmp64_AST = null;
					tmp64_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp64_AST);
					match(STRING);
					callout_arg_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_16);
			}
			returnAST = callout_arg_AST;
		} finally { // debugging
			traceOut("callout_arg");
		}
	}
	
	public final void expr_l0() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l0");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l0_AST = null;
			AST left_AST = null;
			AST op_AST = null;
			AST right_AST = null;
			
			try {      // for error handling
				expr_l1();
				left_AST = (AST)returnAST;
				{
				if ((LA(1)==AND) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					and_op();
					op_AST = (AST)returnAST;
					expr_l0();
					right_AST = (AST)returnAST;
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_l0_AST = (AST)currentAST.root;
				expr_l0_AST = (AST)astFactory.make( (new ASTArray(3)).add(op_AST).add(left_AST).add(right_AST));
				currentAST.root = expr_l0_AST;
				currentAST.child = expr_l0_AST!=null &&expr_l0_AST.getFirstChild()!=null ?
					expr_l0_AST.getFirstChild() : expr_l0_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_l0_AST;
		} finally { // debugging
			traceOut("expr_l0");
		}
	}
	
	public final void or_op() throws RecognitionException, TokenStreamException {
		
		traceIn("or_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST or_op_AST = null;
			
			try {      // for error handling
				AST tmp65_AST = null;
				tmp65_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp65_AST);
				match(OR);
				or_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = or_op_AST;
		} finally { // debugging
			traceOut("or_op");
		}
	}
	
	public final void expr_l1() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l1");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l1_AST = null;
			
			try {      // for error handling
				expr_l2();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if ((LA(1)==EQ||LA(1)==NEQ) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					eq_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr_l1();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_l1_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_l1_AST;
		} finally { // debugging
			traceOut("expr_l1");
		}
	}
	
	public final void and_op() throws RecognitionException, TokenStreamException {
		
		traceIn("and_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST and_op_AST = null;
			
			try {      // for error handling
				AST tmp66_AST = null;
				tmp66_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp66_AST);
				match(AND);
				and_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = and_op_AST;
		} finally { // debugging
			traceOut("and_op");
		}
	}
	
	public final void expr_l2() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l2");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l2_AST = null;
			
			try {      // for error handling
				expr_l3();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if (((LA(1) >= LT && LA(1) <= GEQ)) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					rel_op();
					astFactory.addASTChild(currentAST, returnAST);
					expr_l2();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_l2_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_l2_AST;
		} finally { // debugging
			traceOut("expr_l2");
		}
	}
	
	public final void eq_op() throws RecognitionException, TokenStreamException {
		
		traceIn("eq_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST eq_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case EQ:
				{
					AST tmp67_AST = null;
					tmp67_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp67_AST);
					match(EQ);
					eq_op_AST = (AST)currentAST.root;
					break;
				}
				case NEQ:
				{
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp68_AST);
					match(NEQ);
					eq_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = eq_op_AST;
		} finally { // debugging
			traceOut("eq_op");
		}
	}
	
	public final void expr_l3() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l3");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l3_AST = null;
			AST left_AST = null;
			AST op_AST = null;
			AST right_AST = null;
			
			try {      // for error handling
				expr_l4();
				left_AST = (AST)returnAST;
				{
				if ((LA(1)==MINUS||LA(1)==PLUS) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					linear_op();
					op_AST = (AST)returnAST;
					expr_l3();
					right_AST = (AST)returnAST;
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_l3_AST = (AST)currentAST.root;
				expr_l3_AST = (AST)astFactory.make( (new ASTArray(3)).add(op_AST).add(left_AST).add(right_AST));
				currentAST.root = expr_l3_AST;
				currentAST.child = expr_l3_AST!=null &&expr_l3_AST.getFirstChild()!=null ?
					expr_l3_AST.getFirstChild() : expr_l3_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_l3_AST;
		} finally { // debugging
			traceOut("expr_l3");
		}
	}
	
	public final void rel_op() throws RecognitionException, TokenStreamException {
		
		traceIn("rel_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST rel_op_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case LT:
				{
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp69_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp70_AST = null;
					tmp70_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp70_AST);
					match(GT);
					break;
				}
				case LEQ:
				{
					AST tmp71_AST = null;
					tmp71_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp71_AST);
					match(LEQ);
					break;
				}
				case GEQ:
				{
					AST tmp72_AST = null;
					tmp72_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp72_AST);
					match(GEQ);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				rel_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = rel_op_AST;
		} finally { // debugging
			traceOut("rel_op");
		}
	}
	
	public final void expr_l4() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l4");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l4_AST = null;
			AST left_AST = null;
			AST op_AST = null;
			AST right_AST = null;
			
			try {      // for error handling
				expr_t();
				left_AST = (AST)returnAST;
				{
				if (((LA(1) >= MUL && LA(1) <= MOD)) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					mul_op();
					op_AST = (AST)returnAST;
					expr_l4();
					right_AST = (AST)returnAST;
				}
				else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_12.member(LA(2))) && (_tokenSet_13.member(LA(3)))) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				expr_l4_AST = (AST)currentAST.root;
				expr_l4_AST = (AST)astFactory.make( (new ASTArray(3)).add(op_AST).add(left_AST).add(right_AST));
				currentAST.root = expr_l4_AST;
				currentAST.child = expr_l4_AST!=null &&expr_l4_AST.getFirstChild()!=null ?
					expr_l4_AST.getFirstChild() : expr_l4_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_l4_AST;
		} finally { // debugging
			traceOut("expr_l4");
		}
	}
	
	public final void linear_op() throws RecognitionException, TokenStreamException {
		
		traceIn("linear_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST linear_op_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp73_AST = null;
					tmp73_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp73_AST);
					match(PLUS);
					linear_op_AST = (AST)currentAST.root;
					break;
				}
				case MINUS:
				{
					AST tmp74_AST = null;
					tmp74_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp74_AST);
					match(MINUS);
					linear_op_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = linear_op_AST;
		} finally { // debugging
			traceOut("linear_op");
		}
	}
	
	public final void expr_t() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_t");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_t_AST = null;
			
			try {      // for error handling
				if ((LA(1)==NOT) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
					logical_not();
					astFactory.addASTChild(currentAST, returnAST);
					expr_t_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_11.member(LA(2))) && (_tokenSet_17.member(LA(3)))) {
					expr_tm();
					astFactory.addASTChild(currentAST, returnAST);
					expr_t_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_t_AST;
		} finally { // debugging
			traceOut("expr_t");
		}
	}
	
	public final void mul_op() throws RecognitionException, TokenStreamException {
		
		traceIn("mul_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST mul_op_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case MOD:
				{
					AST tmp75_AST = null;
					tmp75_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp75_AST);
					match(MOD);
					break;
				}
				case DIV:
				{
					AST tmp76_AST = null;
					tmp76_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp76_AST);
					match(DIV);
					break;
				}
				case MUL:
				{
					AST tmp77_AST = null;
					tmp77_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp77_AST);
					match(MUL);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				mul_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			}
			returnAST = mul_op_AST;
		} finally { // debugging
			traceOut("mul_op");
		}
	}
	
	public final void logical_not() throws RecognitionException, TokenStreamException {
		
		traceIn("logical_not");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST logical_not_AST = null;
			
			try {      // for error handling
				AST tmp78_AST = null;
				tmp78_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp78_AST);
				match(NOT);
				expr_tm();
				astFactory.addASTChild(currentAST, returnAST);
				logical_not_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = logical_not_AST;
		} finally { // debugging
			traceOut("logical_not");
		}
	}
	
	public final void expr_tm() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_tm");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_tm_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case MINUS:
				{
					unary_minus();
					astFactory.addASTChild(currentAST, returnAST);
					expr_tm_AST = (AST)currentAST.root;
					break;
				}
				case TK_callout:
				case LPAREN:
				case NOT:
				case ID:
				case INTLITERAL:
				case CHAR:
				case BOOLEANLITERAL:
				{
					expr_tf();
					astFactory.addASTChild(currentAST, returnAST);
					expr_tm_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_tm_AST;
		} finally { // debugging
			traceOut("expr_tm");
		}
	}
	
	public final void unary_minus() throws RecognitionException, TokenStreamException {
		
		traceIn("unary_minus");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST unary_minus_AST = null;
			
			try {      // for error handling
				AST tmp79_AST = null;
				tmp79_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp79_AST);
				match(MINUS);
				expr_tf();
				astFactory.addASTChild(currentAST, returnAST);
				unary_minus_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = unary_minus_AST;
		} finally { // debugging
			traceOut("unary_minus");
		}
	}
	
	public final void expr_tf() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_tf");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_tf_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case INTLITERAL:
				case CHAR:
				case BOOLEANLITERAL:
				{
					literal();
					astFactory.addASTChild(currentAST, returnAST);
					expr_tf_AST = (AST)currentAST.root;
					break;
				}
				case NOT:
				{
					AST tmp80_AST = null;
					tmp80_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp80_AST);
					match(NOT);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					expr_tf_AST = (AST)currentAST.root;
					break;
				}
				case LPAREN:
				{
					match(LPAREN);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					expr_tf_AST = (AST)currentAST.root;
					break;
				}
				default:
					if ((LA(1)==ID) && (_tokenSet_18.member(LA(2)))) {
						location();
						astFactory.addASTChild(currentAST, returnAST);
						expr_tf_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==TK_callout||LA(1)==ID) && (LA(2)==LPAREN)) {
						method_call();
						astFactory.addASTChild(currentAST, returnAST);
						expr_tf_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = expr_tf_AST;
		} finally { // debugging
			traceOut("expr_tf");
		}
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		traceIn("literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case INTLITERAL:
				{
					AST tmp83_AST = null;
					tmp83_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp83_AST);
					match(INTLITERAL);
					literal_AST = (AST)currentAST.root;
					break;
				}
				case CHAR:
				{
					AST tmp84_AST = null;
					tmp84_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp84_AST);
					match(CHAR);
					literal_AST = (AST)currentAST.root;
					break;
				}
				case BOOLEANLITERAL:
				{
					AST tmp85_AST = null;
					tmp85_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp85_AST);
					match(BOOLEANLITERAL);
					literal_AST = (AST)currentAST.root;
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			}
			returnAST = literal_AST;
		} finally { // debugging
			traceOut("literal");
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"true\"",
		"\"false\"",
		"\"boolean\"",
		"\"break\"",
		"\"callout\"",
		"\"class\"",
		"\"continue\"",
		"\"else\"",
		"\"for\"",
		"\"if\"",
		"\"int\"",
		"\"return\"",
		"\"void\"",
		"\"while\"",
		"\"Program\"",
		"{",
		"}",
		"LSQUARE",
		"RSQUARE",
		"LPAREN",
		"RPAREN",
		"WS_",
		"SL_COMMENT",
		"ML_COMMENT",
		"NOT",
		"MINUS",
		"MUL",
		"DIV",
		"MOD",
		"PLUS",
		"LT",
		"GT",
		"LEQ",
		"GEQ",
		"EQ",
		"NEQ",
		"AND",
		"OR",
		"ASSIGN",
		"INC_ASSIGN",
		"DEC_ASSIGN",
		"COMMA",
		"SEMI",
		"ID",
		"INTLITERAL",
		"DEC_LITERAL",
		"HEX_LITERAL",
		"BIN_LITERAL",
		"ALPHA_NUM",
		"ALPHA",
		"DIGIT",
		"HEX_DIGIT",
		"BIN_DIGIT",
		"CHAR",
		"STRING",
		"ESC",
		"CHARLIT",
		"MINUS_ASSIGN",
		"BOOLEANLITERAL"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 1130560L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 140737488355328L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 140737489057152L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 140737490189760L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 140737490122176L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 2305856203355324416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 140737490105728L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 70368744177664L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 109950647402496L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 4756223419782004992L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 4756333369894633728L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 7350419949398717888L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 7350419949400882624L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 2305966154000629760L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 8388608L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 35184388866048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 7350419949400815040L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 109950649499648L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	
	}
