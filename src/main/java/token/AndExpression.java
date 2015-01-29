package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class AndExpression extends Token {

  public ArrayList<Token> children;

  public AndExpression(ArrayList<Token> children) {
    super("", TokenType.AndExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
