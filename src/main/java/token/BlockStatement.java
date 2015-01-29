package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class BlockStatement extends Token {

  public ArrayList<Token> children;

  public BlockStatement(ArrayList<Token> children) {
    super("", TokenType.BlockStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
