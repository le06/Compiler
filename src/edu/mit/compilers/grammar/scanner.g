header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

{@SuppressWarnings("unchecked")}
class DecafScanner extends Lexer;
options
{
  k = 2;
}

tokens 
{
  "true";
  "false";
  "boolean";
  "break";
  "callout";
  "class";
  "continue";
  "else";
  "for";
  "if";
  "int";
  "return";
  "void";
  "while"; 
  "Program";
}

// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws CharStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws CharStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";

LSQUARE : "[";
RSQUARE : "]";

LPAREN : "(";
RPAREN : ")";


// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\n' {newline();} | '\t' | '\r') {_ttype = Token.SKIP; };
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };
ML_COMMENT : "/*"
    (
      options {
        generateAmbigWarnings=false;
      }
      :  { LA(2)!='/' }? '*'
      | '\r' '\n' {newline();}
      | '\r' {newline();}
      | '\n' {newline();}
      | ~('*'|'\n'|'\r')
    )*
    "*/"
    {$setType(Token.SKIP);};

NOT : '!';

MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
PLUS : '+';

LT : '<';
GT : '>';
LEQ : "<=";
GEQ : ">=";

EQ : "==";
NEQ : "!=";

AND : "&&";
OR : "||";

ASSIGN : '=';
INC_ASSIGN : "+=";
DEC_ASSIGN : "-=";

COMMA : ',';
SEMI : ';'; 

ID : ALPHA (ALPHA_NUM)*;

INTLITERAL : (DEC_LITERAL | HEX_LITERAL | BIN_LITERAL);
 protected
 DEC_LITERAL : (DIGIT)+;
 protected
 HEX_LITERAL : "0x" (HEX_DIGIT)+;
 protected
 BIN_LITERAL : "0b" (BIN_DIGIT)+;

 protected
 ALPHA_NUM : (ALPHA | DIGIT);

 protected 
 ALPHA : ('a'..'z' | 'A'..'Z' | '_');

 protected
 DIGIT : '0'..'9';

 protected
 HEX_DIGIT : ('0'..'9' | 'a'..'f' | 'A'..'F');

 protected
 BIN_DIGIT : '0' | '1';

CHAR : '\'' CHARLIT '\'';
STRING : '"' (CHARLIT)* '"';
 protected
 ESC :  '\\' ('n'|'t'|'"'|'\\'|'\'');
 protected
 CHARLIT : (ESC|' '..'!'|'#'..'&'|'('..'.'|'0'..'~');
