package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassBodyDeclaration extends Token {

  public ArrayList<Token> children;

  public ClassBodyDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
