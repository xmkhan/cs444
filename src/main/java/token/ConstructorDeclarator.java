package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ConstructorDeclarator extends Token {

  public ArrayList<Token> children;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclarator);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
