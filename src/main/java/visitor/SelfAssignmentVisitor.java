package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.LocalVariableDeclaration;
import token.Name;
import token.Token;
import token.UnaryExpressionNotMinus;

import java.util.List;

/**
 * Responsible for checking that local  declaration assignments do not use the LHS Name on the RHS.
 */
public class SelfAssignmentVisitor extends BaseVisitor {
  private SymbolTable table;
  private String lhs_identifier = null;
  private CompilationUnit unit;

  public SelfAssignmentVisitor(SymbolTable table) {
    this.table = table;
  }

  public void checkSelfAssignment(List<CompilationUnit> units) throws VisitorException {
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    unit = token;
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
    lhs_identifier = token.getIdentifier();
  }

  @Override
  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
    super.visit(token);
    if (token.name != null && token.name.getLexeme().equals(lhs_identifier)) {
      throw new VisitorException("Cannot use local variable before it is defined", token);
    }
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (lhs_identifier != null && token.getLexeme().equals(";")) {
      lhs_identifier = null;
    }
  }
}
