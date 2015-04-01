package visitor;

import algorithm.base.Pair;
import exception.VisitorException;
import token.AbstractMethodDeclaration;
import token.AdditiveExpression;
import token.AndExpression;
import token.ArgumentList;
import token.ArrayAccess;
import token.ArrayCreationExpression;
import token.ArrayType;
import token.Assignment;
import token.AssignmentExpression;
import token.AssignmentOperator;
import token.Block;
import token.BlockStatement;
import token.BlockStatements;
import token.BooleanLiteral;
import token.CastExpression;
import token.CharLiteral;
import token.ClassBody;
import token.ClassBodyDeclaration;
import token.ClassBodyDeclarations;
import token.ClassDeclaration;
import token.ClassInstanceCreationExpression;
import token.ClassMemberDeclaration;
import token.ClassOrInterfaceType;
import token.ClassType;
import token.CompilationUnit;
import token.ConditionalAndExpression;
import token.ConditionalOrExpression;
import token.ConstructorBody;
import token.ConstructorDeclaration;
import token.ConstructorDeclarator;
import token.EmptyStatement;
import token.EqualityExpression;
import token.Expression;
import token.ExpressionStatement;
import token.ExtendsInterfaces;
import token.FieldAccess;
import token.FieldDeclaration;
import token.ForInit;
import token.ForStatement;
import token.ForStatementNoShortIf;
import token.ForUpdate;
import token.FormalParameter;
import token.FormalParameterList;
import token.IfThenElseStatement;
import token.IfThenElseStatementNoShortIf;
import token.IfThenStatement;
import token.ImportDeclaration;
import token.ImportDeclarations;
import token.InclusiveOrExpression;
import token.IntLiteral;
import token.InterfaceBody;
import token.InterfaceDeclaration;
import token.InterfaceMemberDeclaration;
import token.InterfaceMemberDeclarations;
import token.InterfaceType;
import token.InterfaceTypeList;
import token.Interfaces;
import token.LeftHandSide;
import token.Literal;
import token.LocalVariableDeclaration;
import token.LocalVariableDeclarationStatement;
import token.MethodBody;
import token.MethodDeclaration;
import token.MethodDeclarator;
import token.MethodHeader;
import token.MethodInvocation;
import token.Modifier;
import token.Modifiers;
import token.MultiplicativeExpression;
import token.Name;
import token.PackageDeclaration;
import token.Primary;
import token.PrimitiveType;
import token.QualifiedName;
import token.ReferenceType;
import token.RelationalExpression;
import token.ReturnStatement;
import token.SimpleName;
import token.SingleTypeImportDeclaration;
import token.Statement;
import token.StatementExpression;
import token.StatementNoShortIf;
import token.StatementWithoutTrailingSubstatement;
import token.StringLiteral;
import token.Super;
import token.Token;
import token.Type;
import token.TypeDeclaration;
import token.TypeImportOnDemandDeclaration;
import token.UnaryExpression;
import token.UnaryExpressionNotMinus;
import token.VariableDeclarator;
import token.WhileStatement;
import token.WhileStatementNoShortIf;
import util.CodeGenUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Responsible for generating x86 assembly code for the program.
 */
public class CodeGenerationVisitor extends BaseVisitor {

  public PrintStream output;

  private boolean[][] subclassTable;
  private final int numInterfaceMethods;
  private MethodDeclaration[][] selectorIndexTable;

  public CodeGenerationVisitor(boolean[][] subclassTable, int numInterfaceMethods) {
    this.subclassTable = subclassTable;
    this.numInterfaceMethods = numInterfaceMethods;
  }

  public void generateCode(List<CompilationUnit> units) throws FileNotFoundException, VisitorException {

    selectorIndexTable = new MethodDeclaration[units.size()][numInterfaceMethods];
    for (CompilationUnit unit : units) {
      if (unit.typeDeclaration.getDeclaration() instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
        for (int i = 0; i < classDeclaration.interfaceMethods.length; ++i) {
          selectorIndexTable[classDeclaration.classId][i] = classDeclaration.interfaceMethods[i];
        }
      }
    }

    for (CompilationUnit unit : units) {
      output = new PrintStream(new FileOutputStream("output/" + unit.typeDeclaration.getDeclaration().getIdentifier()));
      unit.traverse(this);
    }
  }

  private void genSubtypeTable() {
    output.println("global __subtype_table");
    output.println("__subtype_table: dd");
    output.println(String.format("mov eax, %d",(4 * subclassTable.length)));
    output.println("call __malloc");
    output.println("mov __subtype_table, eax");
    for(int i = 0; i < subclassTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * subclassTable.length)));
      output.println("call __malloc");
      output.println(String.format("mov [__subtype_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__subtype_table + %d]", 4 * i));
      for (int j = 0; j < subclassTable[i].length; ++j) {
        output.println(String.format("mov [ebx + %d], %d", 4 * j, subclassTable[i][j] ? 1 : 0));
      }
    }
  }

  private void genSelectorIndexTable() {
    output.println("global __selector_index_table");
    output.println("__selector_index_table: dd");
    output.println(String.format("mov eax, %d",(4 * selectorIndexTable.length)));
    output.println("call __malloc");
    output.println("mov __selector_index_table, eax");
    for(int i = 0; i < selectorIndexTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * selectorIndexTable.length)));
      output.println("call __malloc");
      output.println(String.format("mov [__selector_index_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__selector_index_table + %d]", 4 * i));
      for (int j = 0; j < selectorIndexTable[i].length; ++j) {
        output.println(String.format("lea [ebx + %d], %s", 4 * j, CodeGenUtils.genLabel(selectorIndexTable[i][j])));
      }
    }
  }

  @Override
  public void visit(ExpressionStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(PackageDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceMemberDeclarations token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodInvocation token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Interfaces token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassMemberDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(WhileStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Modifier token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Primary token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Modifiers token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(SingleTypeImportDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ImportDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBodyDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassOrInterfaceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(QualifiedName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(TypeImportOnDemandDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AssignmentOperator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(SimpleName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ArrayCreationExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CharLiteral token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenElseStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(TypeDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ConstructorDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceTypeList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ReturnStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(BlockStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(UnaryExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AdditiveExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FormalParameterList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(WhileStatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(VariableDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AssignmentExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StatementWithoutTrailingSubstatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Type token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ReferenceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Statement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Assignment token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    output.println("section .text");
  }

  @Override
  public void visit(ConstructorDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IntLiteral token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Super token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(PrimitiveType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodHeader token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(BlockStatements token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FieldAccess token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StatementExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForInit token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(LocalVariableDeclarationStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ArrayAccess token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Expression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ImportDeclarations token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Block token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StringLiteral token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ArrayType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForUpdate token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(EmptyStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Name token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ExtendsInterfaces token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(EqualityExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBodyDeclarations token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ConstructorBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);

    // After generating code for the subtree, at the end we create the vtable entry.
    output.println(String.format("global __vtable_%s", token.getAbsolutePath()));
    output.println(String.format("__vtable_%s: dd", token.getAbsolutePath()));
    output.println(String.format("mov eax, %d", token.vTableSize));
    output.println("call __malloc");
    output.println(String.format("mov __vtable_%s, eax", token.getAbsolutePath()));
    for (int i = 0; i < token.methods.size(); ++i) {
      output.println(String.format("lea [__vtable_%s + %d], %s", token.getAbsolutePath(), 4 * i, CodeGenUtils.genLabel(token.methods.get(i))));
    }
  }

  @Override
  public void visit(InterfaceMemberDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForStatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(BooleanLiteral token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ArgumentList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
  }
}
