package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class FieldDeclaration extends Token {

  public Modifiers modifiers;
  public Type type;
  public VariableDeclarator variableDeclarator;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Modifiers) {
      modifiers = (Modifiers) token;
    } else if (token instanceof Type) {
      type = (Type) token;
    } else if (token instanceof VariableDeclarator) {
      variableDeclarator = (VariableDeclarator) token;
    }
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(variableDeclarator);
    if (modifiers != null) v.visit(modifiers);
    v.visit(this);
  }
}
