package edu.mit.compilers.grammar;

import antlr.CommonAST;
import antlr.Token;

/**
 * Implement a version of an AST node that stores token locations.
 * @author lizfong@mit.edu (Liz Fong)
 */
@SuppressWarnings("serial")
public class LineNumberedAST extends CommonAST {
  private int line = -1;
  private int col = -1;

  @Override
  public void initialize(Token token) {
    super.initialize(token);
    line = token.getLine();
    col = token.getColumn();
  }

  @Override
  public int getLine() {
    // If we have a line, return it directly; otherwise recurse to
    // find the nearest location information first through children and
    // then through siblings.
    if (line != -1) {
      return line;
    } else if (getFirstChild() != null) {
      return getFirstChild().getLine();
    } else if (getNextSibling() != null) {
      return getNextSibling().getLine();
    } else {
      return -1;
    }
  }

  @Override
  public int getColumn() {
    // If we have a column, return it directly; otherwise recurse to
    // find the nearest location information first through children and
    // then through siblings.
    if (col != -1) {
      return col;
    } else if (getFirstChild() != null) {
      return getFirstChild().getColumn();
    } else if (getNextSibling() != null) {
      return getNextSibling().getColumn();
    } else {
      return -1;
    }
  }
}
