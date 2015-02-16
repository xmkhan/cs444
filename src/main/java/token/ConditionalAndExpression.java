package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ConditionalAndExpression extends Token {

  public ConditionalAndExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalAndExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
