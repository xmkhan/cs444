package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ForStatementNoShortIf extends Token {

  public ArrayList<Token> children;

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
