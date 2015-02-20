package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.*;

/**
 * Creates an acyclic class hierarchy graph for all types known to the program.
 */
public class HierarchyGraph {
  public HashMap<String, HierarchyGraphNode> nodes;

  public HierarchyGraph() {
    this.nodes = new HashMap<String, HierarchyGraphNode>();
  }

  /**
   * Add a node to the graph
   * @throws TypeHierarchyException
   */
  public HierarchyGraph addNode(CompilationUnit compilationUnit) throws TypeHierarchyException, DeadCodeException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    String fullNodeName = constructFullName(extractPackageName(compilationUnit), classOrInterface);
    HierarchyGraphNode node = createNodeIfItDoesntExist(fullNodeName);
    node.setImportDeclarations(extractImports(compilationUnit));
    processTypeDeclarationToken(classOrInterface, node);
    return this;
  }

  private String constructFullName(String packageName, Token classOrInterface) throws DeadCodeException {
    if (classOrInterface instanceof ClassDeclaration) {
      return packageName + "." + ((ClassDeclaration) classOrInterface).identifier.getLexeme();
    } else if (classOrInterface instanceof InterfaceDeclaration) {
      return packageName + "." + ((InterfaceDeclaration) classOrInterface).identifier.getLexeme();
    } else {
      throw new DeadCodeException("Expecting a ClassDeclaration or InterfaceDeclaration token but received " + classOrInterface.getTokenType());
    }
  }

  private String extractPackageName(CompilationUnit compilationUnit) {
    if (compilationUnit.children.get(0) instanceof PackageDeclaration) {
      return compilationUnit.children.get(0).getLexeme();
    }
    return "";
  }

  private ImportDeclarations extractImports(CompilationUnit compilationUnit) {
    if (compilationUnit.children.get(0) instanceof ImportDeclarations) {
      return ((ImportDeclarations)compilationUnit.children.get(0));
    }
    return null;
  }


  /**
   * Add the data we need for Hierarchy checking to the node associated to the class or interface
   * we are processing
   */
  private void processTypeDeclarationToken(Token classOrInterface, HierarchyGraphNode node) throws DeadCodeException, TypeHierarchyException {
    if (node == null) {
      throw new DeadCodeException("Failed to fetch or create a HierarchyGraphNode");
    }

    //System.out.println("nmae: " + node.identifier);

    node.classOrInterface = classOrInterface;
    for (Token token : classOrInterface.children) {
      switch (token.getTokenType()) {
        case Modifiers:
          node.modifiers = ((Modifiers) token).getModifiers();
          break;
        case IDENTIFIER:
          node.identifier = token.getLexeme();
          break;
        case Super:
          extend((Super) token, node);
          break;
        case Interfaces:
          implementsInterfaces((Interfaces) token, node);
          break;
        case ExtendsInterfaces:
          extendsInterfaces((ExtendsInterfaces) token, node);
          break;
        case ClassBody:
          List<MethodHeader> methods = new ArrayList<MethodHeader>();
          List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();
          extractMethodHeaders((ClassBody) token, methods, constructors);
          addMethodsToNode(methods, node);
          addConstructorsToNode(constructors, node);
          break;
        case InterfaceBody:
          addMethodsToNode(extractMethodHeaders((InterfaceBody) token), node);
          break;
        case CLASS:
          break;
        case INTERFACE:
          break;
        default:
          throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
      }
    }
  }

  /**
   * Add constructors to node
   */
  private void addConstructorsToNode(List<ConstructorDeclaration> constructors, HierarchyGraphNode node) throws DeadCodeException {
    if (constructors == null) return;
    for (ConstructorDeclaration constructor : constructors) {
      Method method = new Method();
      node.constructors.add(method);
      method.classOrInterfaceName = node.identifier;
      for (Token token : constructor.children) {
        switch (token.getTokenType()) {
          case ConstructorDeclarator:
            method.identifier = ((ConstructorDeclarator)token).getIdentifier().getLexeme();
            method.parameterTypes.addAll(extractParameterTypes(((ConstructorDeclarator) token).getParameterList()));
            break;
          case Modifiers:
            method.addModifiers(((Modifiers)token).getModifiers());
            break;
          case ConstructorBody:
            break;
          default:
            throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
        }
      }
    }
  }

  /**
   * Add method information, such as parameter types and modifiers to the node
   */
  private void addMethodsToNode(List<MethodHeader> methodHeaders, HierarchyGraphNode node) throws DeadCodeException {
    if (methodHeaders == null) return;
    for (MethodHeader methodHeader : methodHeaders) {
      Method method = new Method();
      node.methods.add(method);
      method.classOrInterfaceName = node.identifier;
      for (Token token : methodHeader.children) {
        switch (token.getTokenType()) {
          case Type:
            method.returnType = ((Type) token).getType().getLexeme();
            break;
          case MethodDeclarator:
            method.identifier = ((MethodDeclarator)token).identifier;
            method.parameterTypes.addAll(extractParameterTypes(((MethodDeclarator) token).getParameterList()));
            break;
          case Modifiers:
            method.addModifiers(((Modifiers)token).getModifiers());
            break;
          case VOID:
            method.returnType = TokenType.VOID.toString();
            break;
          default:
            throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
        }
      }
    }
  }

  /**
   * Extract the parameters of the MethodDeclarator passed in
   */
  private ArrayList<Parameter> extractParameterTypes(FormalParameterList parameterList) {
    ArrayList<Parameter> parameterTypes = new ArrayList<Parameter>();

    if (parameterList == null) return parameterTypes;

    for (FormalParameter formalParameter : parameterList.getFormalParameters()) {
      parameterTypes.add(new Parameter(formalParameter.getType().getLexeme(), formalParameter.isArray()));
    }

    return parameterTypes;
  }

  /**
   * Retrieves all the MethodHeaders in the ClassBody passed in
   */
  private void extractMethodHeaders(ClassBody classBody, List<MethodHeader> methods, List<ConstructorDeclaration> constructors) {
    if (classBody != null && classBody.bodyDeclarations == null) return;
    for (ClassBodyDeclaration classBodyDeclaration : classBody.bodyDeclarations.getBodyDeclarations()) {
      if (classBodyDeclaration.isMethod()) {
        methods.add(((MethodDeclaration) (classBodyDeclaration.children.get(0).children.get(0))).methodHeader);
      }
      if (classBodyDeclaration.isConstructor()) {
        constructors.add((ConstructorDeclaration) classBodyDeclaration.declaration);
      }
    }
  }

  private List<MethodHeader> extractMethodHeaders(InterfaceBody interfaceBody) {
    List<MethodHeader> methodHeaders = new ArrayList<MethodHeader>();
    if (interfaceBody != null && interfaceBody.getInterfaceMemberDeclaration() == null) return methodHeaders;
    for (InterfaceMemberDeclaration interfaceMemberDeclaration : interfaceBody.getInterfaceMemberDeclaration().getMemberDeclarations()) {
      methodHeaders.add(interfaceMemberDeclaration.getMethodHeader());
    }
    return methodHeaders;
  }

  /**
   * Handles a class extending another.  Add the extended class to the Hierarchy graph
   * if it does not exist
   * @param extend class getting extended
   * @param node node representing the class extending
   * @throws TypeHierarchyException
   */
  private void extend(Super extend, HierarchyGraphNode node) throws TypeHierarchyException {
    updateNodeRelationships(extend.getType().getLexeme(), node, extend.getTokenType());
  }

  /**
   * Handles a class implementing one or more interfaces.  Adds the implemented
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void implementsInterfaces(Interfaces interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    for (InterfaceType interfaceType : interfaces.interfaceTypeList.types) {
      updateNodeRelationships(interfaceType.getType().getLexeme(), node, interfaces.getTokenType());
    }
  }

  /**
   * Handles an Interface extending one or more interfaces.  Adds the extended extended
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void extendsInterfaces(ExtendsInterfaces interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    for (InterfaceType interfaceType : interfaces.getInterfaceType()) {
      updateNodeRelationships(interfaceType.getType().getLexeme(), node, interfaces.getTokenType());
    }
  }

  /**
   * Set the child/parent relationships. Create a node for the parent if it does not exist
   * @throws TypeHierarchyException
   */
  private void updateNodeRelationships(String name, HierarchyGraphNode child, TokenType tokenType) throws TypeHierarchyException {
    if (child.hasParent(name)) {
      throw new TypeHierarchyException("Interface " + name + " is repeated in the TypeDeclaration of " + child.identifier);
    }

    HierarchyGraphNode parentNode = createNodeIfItDoesntExist(name, child.getImportList());
    parentNode.children.add(child);

    if (tokenType.equals(TokenType.Super) ||
      tokenType.equals(TokenType.EXTENDS) ||
      tokenType.equals(TokenType.ExtendsInterfaces)) {
      child.extendsList.add(parentNode);
    } else {
      child.implementsList.add(parentNode);
    }
  }

  private HierarchyGraphNode createNodeIfItDoesntExist(String name) {
    return createNodeIfItDoesntExist(name, new ArrayList<ImportDeclaration>());
  }

  /**
   * Returns the node corresponding to name
   * Creates a new node if it doesn't exist
   */
  private HierarchyGraphNode createNodeIfItDoesntExist(String name, List<ImportDeclaration> imports) {
    if (nodes.containsKey(name)) {
      return nodes.get(name);
    } else if (imports.size() > 0) {
      // Check to see if a node with identifier import.class name exists
      for (ImportDeclaration imported : imports) {
        if (nodes.containsKey(imported + name)) {
          return nodes.get(imported + name);
        }
      }
    }
    // Node doesn't exist, create a new one
    HierarchyGraphNode node;
    node = new HierarchyGraphNode();
    node.identifier = name;
    nodes.put(name, node);
    return node;
  }
}
