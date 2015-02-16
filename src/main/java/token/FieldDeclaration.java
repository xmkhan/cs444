package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FieldDeclaration extends Declaration {

  public Modifiers modifiers;
  public Type type;
  public VariableDeclarator variableDeclarator;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration, children);
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
    variableDeclarator.accept(v);
    v.visit(modifiers);
    v.visit(this);
  }
}
