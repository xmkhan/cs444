JFLAGS = -J-Xmx256M -cp
JC = javac
default: clean classesdir outputdir cs444
cs444:
	$(JC) $(JFLAGS) src/main/java:. -d classes/  src/main/java/Main.java  src/main/java/dfa/CommentDFA.java  src/main/java/dfa/DFA.java  src/main/java/dfa/IdentifierDFA.java  src/main/java/dfa/LiteralDFA.java  src/main/java/dfa/NumericDFA.java  src/main/java/dfa/ReservedDFA.java  src/main/java/exception/CompilerException.java  src/main/java/exception/DeadCodeException.java  src/main/java/exception/DisambiguityVisitorException.java  src/main/java/exception/EnvironmentBuildingVisitorException.java  src/main/java/exception/LexerException.java  src/main/java/exception/MachineException.java  src/main/java/exception/NameResolutionException.java  src/main/java/exception/ReachabilityVisitorException.java  src/main/java/exception/SelfAssignmentVisitorException.java  src/main/java/exception/StaticEvaluationVisitorException.java  src/main/java/exception/TypeCheckingVisitorException.java  src/main/java/exception/TypeHierarchyException.java  src/main/java/exception/TypeLinkingVisitorException.java  src/main/java/exception/VariableNameResolutionException.java  src/main/java/exception/VisitorException.java  src/main/java/lexer/Lexer.java  src/main/java/symbol/Scope.java  src/main/java/symbol/SymbolTable.java  src/main/java/token/AbstractMethodDeclaration.java  src/main/java/token/AdditiveExpression.java  src/main/java/token/AndExpression.java  src/main/java/token/ArgumentList.java  src/main/java/token/ArrayAccess.java  src/main/java/token/ArrayCreationExpression.java  src/main/java/token/ArrayType.java  src/main/java/token/Assignment.java  src/main/java/token/AssignmentExpression.java  src/main/java/token/AssignmentOperator.java  src/main/java/token/BaseForStatement.java  src/main/java/token/BaseIfThenElse.java  src/main/java/token/BaseMethodDeclaration.java  src/main/java/token/BaseStatement.java  src/main/java/token/BaseWhileStatement.java  src/main/java/token/Block.java  src/main/java/token/BlockStatement.java  src/main/java/token/BlockStatements.java  src/main/java/token/BooleanLiteral.java  src/main/java/token/CastExpression.java  src/main/java/token/CharLiteral.java  src/main/java/token/ClassBody.java  src/main/java/token/ClassBodyDeclaration.java  src/main/java/token/ClassBodyDeclarations.java  src/main/java/token/ClassDeclaration.java  src/main/java/token/ClassInstanceCreationExpression.java  src/main/java/token/ClassMemberDeclaration.java  src/main/java/token/ClassOrInterfaceType.java  src/main/java/token/ClassType.java  src/main/java/token/CompilationUnit.java  src/main/java/token/ConditionalAndExpression.java  src/main/java/token/ConditionalOrExpression.java  src/main/java/token/ConstructorBody.java  src/main/java/token/ConstructorDeclaration.java  src/main/java/token/ConstructorDeclarator.java  src/main/java/token/Declaration.java  src/main/java/token/EmptyStatement.java  src/main/java/token/EqualityExpression.java  src/main/java/token/Expression.java  src/main/java/token/ExpressionStatement.java  src/main/java/token/ExtendsInterfaces.java  src/main/java/token/FieldAccess.java  src/main/java/token/FieldDeclaration.java  src/main/java/token/ForInit.java  src/main/java/token/FormalParameter.java  src/main/java/token/FormalParameterList.java  src/main/java/token/ForStatement.java  src/main/java/token/ForStatementNoShortIf.java  src/main/java/token/ForUpdate.java  src/main/java/token/IfThenElseStatement.java  src/main/java/token/IfThenElseStatementNoShortIf.java  src/main/java/token/IfThenStatement.java  src/main/java/token/ImportDeclaration.java  src/main/java/token/ImportDeclarations.java  src/main/java/token/InclusiveOrExpression.java  src/main/java/token/InterfaceBody.java  src/main/java/token/InterfaceDeclaration.java  src/main/java/token/InterfaceMemberDeclaration.java  src/main/java/token/InterfaceMemberDeclarations.java  src/main/java/token/Interfaces.java  src/main/java/token/InterfaceType.java  src/main/java/token/InterfaceTypeList.java  src/main/java/token/IntLiteral.java  src/main/java/token/LeftHandSide.java  src/main/java/token/Literal.java  src/main/java/token/LocalVariableDeclaration.java  src/main/java/token/LocalVariableDeclarationStatement.java  src/main/java/token/MethodBody.java  src/main/java/token/MethodDeclaration.java  src/main/java/token/MethodDeclarator.java  src/main/java/token/MethodHeader.java  src/main/java/token/MethodInvocation.java  src/main/java/token/Modifier.java  src/main/java/token/Modifiers.java  src/main/java/token/MultiplicativeExpression.java  src/main/java/token/Name.java  src/main/java/token/PackageDeclaration.java  src/main/java/token/Primary.java  src/main/java/token/PrimitiveType.java  src/main/java/token/QualifiedName.java  src/main/java/token/ReferenceType.java  src/main/java/token/RelationalExpression.java  src/main/java/token/ReturnStatement.java  src/main/java/token/SimpleName.java  src/main/java/token/SingleTypeImportDeclaration.java  src/main/java/token/Statement.java  src/main/java/token/StatementExpression.java  src/main/java/token/StatementNoShortIf.java  src/main/java/token/StatementWithoutTrailingSubstatement.java  src/main/java/token/StringLiteral.java  src/main/java/token/Super.java  src/main/java/token/Token.java  src/main/java/token/TokenType.java  src/main/java/token/Type.java  src/main/java/token/TypeDeclaration.java  src/main/java/token/TypeImportOnDemandDeclaration.java  src/main/java/token/UnaryExpression.java  src/main/java/token/UnaryExpressionNotMinus.java  src/main/java/token/VariableDeclarator.java  src/main/java/token/WhileStatement.java  src/main/java/token/WhileStatementNoShortIf.java  src/main/java/util/CodeGenUtils.java  src/main/java/visitor/BaseVisitor.java  src/main/java/visitor/CodeGenerationVisitor.java  src/main/java/visitor/DisambiguityVisitor.java  src/main/java/visitor/EnvironmentBuildingVisitor.java  src/main/java/visitor/GenericCheckVisitor.java  src/main/java/visitor/PreliminaryCodeGenerationVisitor.java  src/main/java/visitor/ReachabilityVisitor.java  src/main/java/visitor/SelfAssignmentVisitor.java  src/main/java/visitor/StaticEvaluationVisitor.java  src/main/java/visitor/TypeCheckingVisitor.java  src/main/java/visitor/TypeCheckToken.java  src/main/java/visitor/TypeLinkingVisitor.java  src/main/java/visitor/VariableScopeVisitor.java  src/main/java/visitor/Visitee.java  src/main/java/visitor/Visitor.java  src/main/java/algorithm/base/Pair.java  src/main/java/algorithm/trie/Trie.java  src/main/java/type/hierarchy/CompilationUnitsToHierarchyGraphConverter.java  src/main/java/type/hierarchy/HierarchyChecker.java  src/main/java/type/hierarchy/HierarchyGraph.java  src/main/java/type/hierarchy/HierarchyGraphNode.java  src/main/java/type/hierarchy/HierarchyUtil.java  src/main/java/type/hierarchy/Method.java  src/main/java/type/hierarchy/Parameter.java  src/main/java/algorithm/name/resolution/NameResolutionAlgorithm.java  src/main/java/algorithm/name/resolution/VariableNameResolutionAlgorithm.java  src/main/java/algorithm/parsing/lr/ShiftReduceAlgorithm.java  src/main/java/algorithm/parsing/lr/machine/Machine.java  src/main/java/algorithm/parsing/lr/machine/MachineState.java
classesdir:
	mkdir classes

outputdir:
	mkdir output
clean:
	rm -rf *.class classes output
