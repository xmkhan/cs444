package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Assignment extends Token {

  public ArrayList<Token> children;

  public Assignment(ArrayList<Token> children) {
    super("", TokenType.Assignment);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
