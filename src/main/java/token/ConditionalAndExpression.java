package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ConditionalAndExpression extends Token {

  public ArrayList<Token> children;

  public ConditionalAndExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalAndExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
