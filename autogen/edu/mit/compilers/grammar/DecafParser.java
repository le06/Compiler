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
					array_dec();
					astFactory.addASTChild(currentAST, returnAST);
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
							AST tmp8_AST = null;
							tmp8_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp8_AST);
							match(ID);
						}
						else if ((LA(1)==ID) && (LA(2)==LSQUARE)) {
							array_dec();
							astFactory.addASTChild(currentAST, returnAST);
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
				field_dec_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FIELD,"field")).add(field_dec_AST));
				currentAST.root = field_dec_AST;
				currentAST.child = field_dec_AST!=null &&field_dec_AST.getFirstChild()!=null ?
					field_dec_AST.getFirstChild() : field_dec_AST;
				currentAST.advanceChildToEnd();
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
					AST tmp10_AST = null;
					tmp10_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp10_AST);
					match(TK_void);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp11_AST = null;
				tmp11_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp11_AST);
				match(ID);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case TK_boolean:
				case TK_int:
				{
					method_param();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop16:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							method_param();
							astFactory.addASTChild(currentAST, returnAST);
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
				method_dec_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(METHOD,"method")).add(method_dec_AST));
				currentAST.root = method_dec_AST;
				currentAST.child = method_dec_AST!=null &&method_dec_AST.getFirstChild()!=null ?
					method_dec_AST.getFirstChild() : method_dec_AST;
				currentAST.advanceChildToEnd();
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
					AST tmp15_AST = null;
					tmp15_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp15_AST);
					match(TK_int);
					type_AST = (AST)currentAST.root;
					break;
				}
				case TK_boolean:
				{
					AST tmp16_AST = null;
					tmp16_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp16_AST);
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
	
	public final void array_dec() throws RecognitionException, TokenStreamException {
		
		traceIn("array_dec");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST array_dec_AST = null;
			
			try {      // for error handling
				AST tmp17_AST = null;
				tmp17_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp17_AST);
				match(ID);
				match(LSQUARE);
				int_literal();
				astFactory.addASTChild(currentAST, returnAST);
				match(RSQUARE);
				array_dec_AST = (AST)currentAST.root;
				array_dec_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY,"array")).add(array_dec_AST));
				currentAST.root = array_dec_AST;
				currentAST.child = array_dec_AST!=null &&array_dec_AST.getFirstChild()!=null ?
					array_dec_AST.getFirstChild() : array_dec_AST;
				currentAST.advanceChildToEnd();
				array_dec_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			}
			returnAST = array_dec_AST;
		} finally { // debugging
			traceOut("array_dec");
		}
	}
	
	public final void int_literal() throws RecognitionException, TokenStreamException {
		
		traceIn("int_literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST int_literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case DEC_LITERAL:
				{
					AST tmp20_AST = null;
					tmp20_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp20_AST);
					match(DEC_LITERAL);
					int_literal_AST = (AST)currentAST.root;
					break;
				}
				case HEX_LITERAL:
				{
					AST tmp21_AST = null;
					tmp21_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp21_AST);
					match(HEX_LITERAL);
					int_literal_AST = (AST)currentAST.root;
					break;
				}
				case BIN_LITERAL:
				{
					AST tmp22_AST = null;
					tmp22_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp22_AST);
					match(BIN_LITERAL);
					int_literal_AST = (AST)currentAST.root;
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
				recover(ex,_tokenSet_4);
			}
			returnAST = int_literal_AST;
		} finally { // debugging
			traceOut("int_literal");
		}
	}
	
	public final void method_param() throws RecognitionException, TokenStreamException {
		
		traceIn("method_param");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST method_param_AST = null;
			AST left_AST = null;
			Token  right = null;
			AST right_AST = null;
			
			try {      // for error handling
				type();
				left_AST = (AST)returnAST;
				right = LT(1);
				right_AST = astFactory.create(right);
				match(ID);
				method_param_AST = (AST)currentAST.root;
				method_param_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PARAM,"param")).add(left_AST).add(right_AST));
				currentAST.root = method_param_AST;
				currentAST.child = method_param_AST!=null &&method_param_AST.getFirstChild()!=null ?
					method_param_AST.getFirstChild() : method_param_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			}
			returnAST = method_param_AST;
		} finally { // debugging
			traceOut("method_param");
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
				_loop20:
				do {
					switch ( LA(1)) {
					case TK_boolean:
					case TK_int:
					{
						var_decl();
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
					case ID:
					{
						statement();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						break _loop20;
					}
					}
				} while (true);
				}
				match(RCURLY);
				block_AST = (AST)currentAST.root;
				block_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BLOCK,"block")).add(block_AST));
				currentAST.root = block_AST;
				currentAST.child = block_AST!=null &&block_AST.getFirstChild()!=null ?
					block_AST.getFirstChild() : block_AST;
				currentAST.advanceChildToEnd();
				block_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_6);
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
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(ID);
				{
				_loop25:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						AST tmp27_AST = null;
						tmp27_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp27_AST);
						match(ID);
					}
					else {
						break _loop25;
					}
					
				} while (true);
				}
				match(SEMI);
				var_decl_AST = (AST)currentAST.root;
				var_decl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(VAR_DECL,"declaration")).add(var_decl_AST));
				currentAST.root = var_decl_AST;
				currentAST.child = var_decl_AST!=null &&var_decl_AST.getFirstChild()!=null ?
					var_decl_AST.getFirstChild() : var_decl_AST;
				currentAST.advanceChildToEnd();
				var_decl_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_7);
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
					if_statement();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_for:
				{
					for_statement();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_while:
				{
					AST tmp29_AST = null;
					tmp29_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp29_AST);
					match(TK_while);
					match(LPAREN);
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					block();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_return:
				{
					AST tmp32_AST = null;
					tmp32_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp32_AST);
					match(TK_return);
					{
					switch ( LA(1)) {
					case TK_true:
					case TK_false:
					case TK_callout:
					case LPAREN:
					case NOT:
					case MINUS:
					case ID:
					case DEC_LITERAL:
					case HEX_LITERAL:
					case BIN_LITERAL:
					case CHAR:
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
					AST tmp34_AST = null;
					tmp34_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp34_AST);
					match(TK_break);
					match(SEMI);
					statement_AST = (AST)currentAST.root;
					break;
				}
				case TK_continue:
				{
					AST tmp36_AST = null;
					tmp36_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp36_AST);
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
					if ((LA(1)==ID) && (_tokenSet_8.member(LA(2)))) {
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
	
	public final void line() throws RecognitionException, TokenStreamException {
		
		traceIn("line");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST line_AST = null;
			
			try {      // for error handling
				{
				switch ( LA(1)) {
				case TK_boolean:
				case TK_int:
				{
					var_decl();
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
				case ID:
				{
					statement();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				line_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			}
			returnAST = line_AST;
		} finally { // debugging
			traceOut("line");
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
				recover(ex,_tokenSet_9);
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
					fn_call();
					astFactory.addASTChild(currentAST, returnAST);
					method_call_AST = (AST)currentAST.root;
					break;
				}
				case TK_callout:
				{
					callout();
					astFactory.addASTChild(currentAST, returnAST);
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
				recover(ex,_tokenSet_4);
			}
			returnAST = method_call_AST;
		} finally { // debugging
			traceOut("method_call");
		}
	}
	
	public final void if_statement() throws RecognitionException, TokenStreamException {
		
		traceIn("if_statement");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST if_statement_AST = null;
			AST cond_AST = null;
			AST if_block_AST = null;
			AST else_block_AST = null;
			
			try {      // for error handling
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				match(TK_if);
				match(LPAREN);
				expr();
				cond_AST = (AST)returnAST;
				match(RPAREN);
				block();
				if_block_AST = (AST)returnAST;
				{
				switch ( LA(1)) {
				case TK_else:
				{
					match(TK_else);
					block();
					else_block_AST = (AST)returnAST;
					break;
				}
				case EOF:
				case TK_boolean:
				case TK_break:
				case TK_callout:
				case TK_continue:
				case TK_for:
				case TK_if:
				case TK_int:
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
				if_statement_AST = (AST)currentAST.root;
				if_statement_AST = (AST)astFactory.make( (new ASTArray(4)).add(tmp40_AST).add(cond_AST).add(if_block_AST).add(else_block_AST));
				currentAST.root = if_statement_AST;
				currentAST.child = if_statement_AST!=null &&if_statement_AST.getFirstChild()!=null ?
					if_statement_AST.getFirstChild() : if_statement_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			}
			returnAST = if_statement_AST;
		} finally { // debugging
			traceOut("if_statement");
		}
	}
	
	public final void for_statement() throws RecognitionException, TokenStreamException {
		
		traceIn("for_statement");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST for_statement_AST = null;
			
			try {      // for error handling
				AST tmp44_AST = null;
				tmp44_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp44_AST);
				match(TK_for);
				match(LPAREN);
				AST tmp46_AST = null;
				tmp46_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp46_AST);
				match(ID);
				match(ASSIGN);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMI);
				expr();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				for_statement_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			}
			returnAST = for_statement_AST;
		} finally { // debugging
			traceOut("for_statement");
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
				_loop47:
				do {
					if ((LA(1)==OR) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						AST tmp50_AST = null;
						tmp50_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp50_AST);
						match(OR);
						expr_l0();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop47;
					}
					
				} while (true);
				}
				expr_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
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
				if ((LA(1)==ID) && (_tokenSet_12.member(LA(2)))) {
					AST tmp51_AST = null;
					tmp51_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp51_AST);
					match(ID);
					location_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==LSQUARE)) {
					array_access();
					astFactory.addASTChild(currentAST, returnAST);
					location_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_12);
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
					AST tmp52_AST = null;
					tmp52_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp52_AST);
					match(ASSIGN);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				case INC_ASSIGN:
				{
					AST tmp53_AST = null;
					tmp53_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp53_AST);
					match(INC_ASSIGN);
					assign_op_AST = (AST)currentAST.root;
					break;
				}
				case DEC_ASSIGN:
				{
					AST tmp54_AST = null;
					tmp54_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp54_AST);
					match(DEC_ASSIGN);
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
	
	public final void fn_call() throws RecognitionException, TokenStreamException {
		
		traceIn("fn_call");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST fn_call_AST = null;
			
			try {      // for error handling
				method_name();
				astFactory.addASTChild(currentAST, returnAST);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case TK_true:
				case TK_false:
				case TK_callout:
				case LPAREN:
				case NOT:
				case MINUS:
				case ID:
				case DEC_LITERAL:
				case HEX_LITERAL:
				case BIN_LITERAL:
				case CHAR:
				{
					expr();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop38:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							expr();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop38;
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
				fn_call_AST = (AST)currentAST.root;
				fn_call_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FN_CALL,"function call")).add(fn_call_AST));
				currentAST.root = fn_call_AST;
				currentAST.child = fn_call_AST!=null &&fn_call_AST.getFirstChild()!=null ?
					fn_call_AST.getFirstChild() : fn_call_AST;
				currentAST.advanceChildToEnd();
				fn_call_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = fn_call_AST;
		} finally { // debugging
			traceOut("fn_call");
		}
	}
	
	public final void callout() throws RecognitionException, TokenStreamException {
		
		traceIn("callout");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST callout_AST = null;
			
			try {      // for error handling
				AST tmp58_AST = null;
				tmp58_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp58_AST);
				match(TK_callout);
				match(LPAREN);
				AST tmp60_AST = null;
				tmp60_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp60_AST);
				match(STRING);
				{
				_loop41:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						callout_arg();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop41;
					}
					
				} while (true);
				}
				match(RPAREN);
				callout_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = callout_AST;
		} finally { // debugging
			traceOut("callout");
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
				recover(ex,_tokenSet_13);
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
				case TK_true:
				case TK_false:
				case TK_callout:
				case LPAREN:
				case NOT:
				case MINUS:
				case ID:
				case DEC_LITERAL:
				case HEX_LITERAL:
				case BIN_LITERAL:
				case CHAR:
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
				recover(ex,_tokenSet_5);
			}
			returnAST = callout_arg_AST;
		} finally { // debugging
			traceOut("callout_arg");
		}
	}
	
	public final void array_access() throws RecognitionException, TokenStreamException {
		
		traceIn("array_access");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST array_access_AST = null;
			Token  left = null;
			AST left_AST = null;
			AST right_AST = null;
			
			try {      // for error handling
				left = LT(1);
				left_AST = astFactory.create(left);
				match(ID);
				match(LSQUARE);
				expr();
				right_AST = (AST)returnAST;
				match(RSQUARE);
				array_access_AST = (AST)currentAST.root;
				array_access_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ARRAY_ACCESS,"array access")).add(left_AST).add(right_AST));
				currentAST.root = array_access_AST;
				currentAST.child = array_access_AST!=null &&array_access_AST.getFirstChild()!=null ?
					array_access_AST.getFirstChild() : array_access_AST;
				currentAST.advanceChildToEnd();
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			}
			returnAST = array_access_AST;
		} finally { // debugging
			traceOut("array_access");
		}
	}
	
	public final void expr_l0() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l0");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l0_AST = null;
			
			try {      // for error handling
				expr_l1();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop50:
				do {
					if ((LA(1)==AND) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						AST tmp67_AST = null;
						tmp67_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp67_AST);
						match(AND);
						expr_l1();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop50;
					}
					
				} while (true);
				}
				expr_l0_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_l0_AST;
		} finally { // debugging
			traceOut("expr_l0");
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
				_loop54:
				do {
					if ((LA(1)==EQ||LA(1)==NEQ) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						{
						switch ( LA(1)) {
						case EQ:
						{
							AST tmp68_AST = null;
							tmp68_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp68_AST);
							match(EQ);
							break;
						}
						case NEQ:
						{
							AST tmp69_AST = null;
							tmp69_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp69_AST);
							match(NEQ);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						expr_l2();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop54;
					}
					
				} while (true);
				}
				expr_l1_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_l1_AST;
		} finally { // debugging
			traceOut("expr_l1");
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
				_loop58:
				do {
					if (((LA(1) >= LT && LA(1) <= GEQ)) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						{
						switch ( LA(1)) {
						case LT:
						{
							AST tmp70_AST = null;
							tmp70_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp70_AST);
							match(LT);
							break;
						}
						case GT:
						{
							AST tmp71_AST = null;
							tmp71_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp71_AST);
							match(GT);
							break;
						}
						case LEQ:
						{
							AST tmp72_AST = null;
							tmp72_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp72_AST);
							match(LEQ);
							break;
						}
						case GEQ:
						{
							AST tmp73_AST = null;
							tmp73_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp73_AST);
							match(GEQ);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						expr_l3();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop58;
					}
					
				} while (true);
				}
				expr_l2_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_l2_AST;
		} finally { // debugging
			traceOut("expr_l2");
		}
	}
	
	public final void expr_l3() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l3");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l3_AST = null;
			
			try {      // for error handling
				expr_l4();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop62:
				do {
					if ((LA(1)==MINUS||LA(1)==PLUS) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						{
						switch ( LA(1)) {
						case PLUS:
						{
							AST tmp74_AST = null;
							tmp74_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp74_AST);
							match(PLUS);
							break;
						}
						case MINUS:
						{
							AST tmp75_AST = null;
							tmp75_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp75_AST);
							match(MINUS);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						expr_l4();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop62;
					}
					
				} while (true);
				}
				expr_l3_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_l3_AST;
		} finally { // debugging
			traceOut("expr_l3");
		}
	}
	
	public final void expr_l4() throws RecognitionException, TokenStreamException {
		
		traceIn("expr_l4");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST expr_l4_AST = null;
			
			try {      // for error handling
				expr_t();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop66:
				do {
					if (((LA(1) >= MUL && LA(1) <= MOD)) && (_tokenSet_10.member(LA(2))) && (_tokenSet_11.member(LA(3)))) {
						{
						switch ( LA(1)) {
						case MOD:
						{
							AST tmp76_AST = null;
							tmp76_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp76_AST);
							match(MOD);
							break;
						}
						case DIV:
						{
							AST tmp77_AST = null;
							tmp77_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp77_AST);
							match(DIV);
							break;
						}
						case MUL:
						{
							AST tmp78_AST = null;
							tmp78_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp78_AST);
							match(MUL);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						expr_t();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop66;
					}
					
				} while (true);
				}
				expr_l4_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_l4_AST;
		} finally { // debugging
			traceOut("expr_l4");
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
				else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_11.member(LA(2))) && (_tokenSet_14.member(LA(3)))) {
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
				recover(ex,_tokenSet_4);
			}
			returnAST = expr_t_AST;
		} finally { // debugging
			traceOut("expr_t");
		}
	}
	
	public final void logical_not() throws RecognitionException, TokenStreamException {
		
		traceIn("logical_not");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST logical_not_AST = null;
			
			try {      // for error handling
				AST tmp79_AST = null;
				tmp79_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp79_AST);
				match(NOT);
				expr_tm();
				astFactory.addASTChild(currentAST, returnAST);
				logical_not_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
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
				case TK_true:
				case TK_false:
				case TK_callout:
				case LPAREN:
				case NOT:
				case ID:
				case DEC_LITERAL:
				case HEX_LITERAL:
				case BIN_LITERAL:
				case CHAR:
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
				recover(ex,_tokenSet_4);
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
				AST tmp80_AST = null;
				tmp80_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp80_AST);
				match(MINUS);
				expr_tf();
				astFactory.addASTChild(currentAST, returnAST);
				unary_minus_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_4);
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
				case TK_true:
				case TK_false:
				case DEC_LITERAL:
				case HEX_LITERAL:
				case BIN_LITERAL:
				case CHAR:
				{
					literal();
					astFactory.addASTChild(currentAST, returnAST);
					expr_tf_AST = (AST)currentAST.root;
					break;
				}
				case NOT:
				{
					AST tmp81_AST = null;
					tmp81_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp81_AST);
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
					if ((LA(1)==ID) && (_tokenSet_15.member(LA(2)))) {
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
				recover(ex,_tokenSet_4);
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
				case DEC_LITERAL:
				case HEX_LITERAL:
				case BIN_LITERAL:
				{
					int_literal();
					astFactory.addASTChild(currentAST, returnAST);
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
				case TK_true:
				case TK_false:
				{
					bool_literal();
					astFactory.addASTChild(currentAST, returnAST);
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
				recover(ex,_tokenSet_4);
			}
			returnAST = literal_AST;
		} finally { // debugging
			traceOut("literal");
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
					AST tmp85_AST = null;
					tmp85_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp85_AST);
					match(MOD);
					break;
				}
				case DIV:
				{
					AST tmp86_AST = null;
					tmp86_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp86_AST);
					match(DIV);
					break;
				}
				case MUL:
				{
					AST tmp87_AST = null;
					tmp87_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp87_AST);
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
				recover(ex,_tokenSet_0);
			}
			returnAST = mul_op_AST;
		} finally { // debugging
			traceOut("mul_op");
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
					AST tmp88_AST = null;
					tmp88_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp88_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp89_AST = null;
					tmp89_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp89_AST);
					match(GT);
					break;
				}
				case LEQ:
				{
					AST tmp90_AST = null;
					tmp90_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp90_AST);
					match(LEQ);
					break;
				}
				case GEQ:
				{
					AST tmp91_AST = null;
					tmp91_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp91_AST);
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
				recover(ex,_tokenSet_0);
			}
			returnAST = rel_op_AST;
		} finally { // debugging
			traceOut("rel_op");
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
					AST tmp92_AST = null;
					tmp92_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp92_AST);
					match(EQ);
					eq_op_AST = (AST)currentAST.root;
					break;
				}
				case NEQ:
				{
					AST tmp93_AST = null;
					tmp93_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp93_AST);
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
				recover(ex,_tokenSet_0);
			}
			returnAST = eq_op_AST;
		} finally { // debugging
			traceOut("eq_op");
		}
	}
	
	public final void or_op() throws RecognitionException, TokenStreamException {
		
		traceIn("or_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST or_op_AST = null;
			
			try {      // for error handling
				AST tmp94_AST = null;
				tmp94_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(OR);
				or_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			}
			returnAST = or_op_AST;
		} finally { // debugging
			traceOut("or_op");
		}
	}
	
	public final void and_op() throws RecognitionException, TokenStreamException {
		
		traceIn("and_op");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST and_op_AST = null;
			
			try {      // for error handling
				AST tmp95_AST = null;
				tmp95_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp95_AST);
				match(AND);
				and_op_AST = (AST)currentAST.root;
			}
			catch (RecognitionException ex) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			}
			returnAST = and_op_AST;
		} finally { // debugging
			traceOut("and_op");
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
					AST tmp96_AST = null;
					tmp96_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp96_AST);
					match(PLUS);
					linear_op_AST = (AST)currentAST.root;
					break;
				}
				case MINUS:
				{
					AST tmp97_AST = null;
					tmp97_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp97_AST);
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
				recover(ex,_tokenSet_0);
			}
			returnAST = linear_op_AST;
		} finally { // debugging
			traceOut("linear_op");
		}
	}
	
	public final void bool_literal() throws RecognitionException, TokenStreamException {
		
		traceIn("bool_literal");
		try { // debugging
			returnAST = null;
			ASTPair currentAST = new ASTPair();
			AST bool_literal_AST = null;
			
			try {      // for error handling
				switch ( LA(1)) {
				case TK_true:
				{
					AST tmp98_AST = null;
					tmp98_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp98_AST);
					match(TK_true);
					bool_literal_AST = (AST)currentAST.root;
					break;
				}
				case TK_false:
				{
					AST tmp99_AST = null;
					tmp99_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp99_AST);
					match(TK_false);
					bool_literal_AST = (AST)currentAST.root;
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
				recover(ex,_tokenSet_4);
			}
			returnAST = bool_literal_AST;
		} finally { // debugging
			traceOut("bool_literal");
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
		"BLOCK",
		"BLOCK_LINE",
		"VAR_DECL",
		"METHOD",
		"FIELD",
		"ARRAY",
		"ARRAY_ACCESS",
		"FN_CALL",
		"FOR_INIT",
		"PARAM"
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
		long[] data = { 105553116266496L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 109950646878208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 35184388866048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 140737490189762L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 140737490122178L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 30786327674880L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 70368744177664L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 74168657176953136L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 74278607289057584L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 140736972455936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 8388608L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 218424581692257778L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 109950648975360L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	
	}
