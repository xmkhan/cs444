package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class QualifiedName extends Token {

  public QualifiedName(ArrayList<Token> children) {
    super(children.get(0).getLexeme() + children.get(1).getLexeme() + children.get(2).getLexeme(),
        TokenType.QualifiedName, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
