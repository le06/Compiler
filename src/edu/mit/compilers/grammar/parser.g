header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

class DecafParser extends Parser;
options
{
  importVocab = DecafScanner;
  k = 3;
  buildAST = true;
}

// Java glue code that makes error reporting easier.
// You can insert arbitrary Java code into your parser/lexer this way.
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
}

program: TK_class^ TK_Program LCURLY! (field_dec)* (method_dec)* RCURLY! EOF!;

field_dec : type (ID | ID LSQUARE! INTLITERAL RSQUARE!) 
                          (COMMA! (ID | ID LSQUARE! INTLITERAL RSQUARE!))* SEMI!;

method_dec : (type | TK_void^) ID LPAREN! ((type ID) (COMMA! type ID)*)? RPAREN! block;

block : LCURLY! (var_decl)* (statement)* RCURLY!;

var_decl : type ID (COMMA! ID)* SEMI!;

type : TK_int | TK_boolean;

statement :  assignment SEMI!
           | method_call SEMI!
           | TK_if^ (expr) block (TK_else block)?
           | TK_for^ (ID ASSIGN expr COMMA! expr) block
           | TK_while^ (expr) block
           | TK_return^ (expr)? SEMI!
           | TK_break SEMI!
           | TK_continue SEMI!
           | block;

assignment! : left:location op:assign_op right:expr 
			{ #assignment = #(op, left, right); } ;

assign_op :  ASSIGN
           | INC_ASSIGN
           | MINUS_ASSIGN;

method_call :  method_name LPAREN! (expr (COMMA! expr)*)? RPAREN!
             | TK_callout LPAREN! STRING (COMMA! callout_arg)* RPAREN!;

method_name : ID;

location :  ID
          | ID LSQUARE! expr RSQUARE!;


/*
 * All of the expr_xxx rules enforce order of operations.
 * The expr_lx rules (standing for expression level x) 
 * correspond to binary operators, and are ordered from 
 * least binding to most binding (OR -> {* / %}).
 *
 * the expr_tx rules enforce order of operations for unary
 * operators -- expr_t corresponds to logical not, expr_tm
 * corresponds to a unary minus, and expr_tf is the lowest
 * level rule, corresponding to all of the other possible 
 * productions of expr.                                     */
expr! : left:expr_l0 (op:or_op right:expr)? 
           { #expr = #(op, left, right); } ;

expr_l0! : left:expr_l1 (op:and_op right:expr_l0)?
           { #expr_l0 = #(op, left, right); } ;

expr_l1! : left:expr_l2 (op:eq_op right:expr_l1)? 
           { #expr_l1 = #(op, left, right); } ;

expr_l2! : left:expr_l3 (op:rel_op right:expr_l2)? 
		   { #expr_l2 = #(op, left, right); } ;

expr_l3! : left:expr_l4 (op:linear_op right:expr_l3)?
           { #expr_l3 = #(op, left, right); } ;

expr_l4! : left:expr_t (op:mul_op right:expr_l4)?
           { #expr_l4 = #(op, left, right); } ;

expr_t : logical_not | expr_tm;
logical_not : NOT^ expr_tm;

expr_tm : unary_minus | expr_tf; 
unary_minus : MINUS^ expr_tf;

expr_tf :  location
      | method_call
      | literal 
      | NOT^ expr
      | LPAREN! expr RPAREN!; 

callout_arg: expr | STRING;

mul_op : (MOD | DIV | MUL);
rel_op : (LT | GT | LEQ | GEQ);
eq_op : EQ | NEQ;
or_op : OR;
and_op : AND;
linear_op : PLUS | MINUS;

literal : INTLITERAL | CHAR | BOOLEANLITERAL;
