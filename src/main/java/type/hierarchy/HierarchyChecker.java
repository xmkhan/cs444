package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Responsible for performing Class Hierarchy checks
 */
public class HierarchyChecker {
  private HierarchyGraph graph;

  public HierarchyChecker() {
    this.graph = new HierarchyGraph();
  }

  public void reset() {
    this.graph.nodes.clear();
  }

  public void verifyClassAndInterfaceHierarchy(List<CompilationUnit> compilationUnits) throws TypeHierarchyException, DeadCodeException {
    createHierarchyGraph(compilationUnits);
    verifyHierarchyGraph();
  }

  /**
   * Creates a hierarchy graph of all the interfaces and classes being compiled
   * @param compilationUnits list of all the CompilationUnits (one per input file)
   * @throws TypeHierarchyException
   */
  private void createHierarchyGraph(List<CompilationUnit> compilationUnits) throws TypeHierarchyException, DeadCodeException {
    for (CompilationUnit compilationUnit : compilationUnits) {
      addNode(compilationUnit);
    }
  }

  /**
   * Adds a node to the hierarchy graph
   * @param compilationUnit Search this compilation unit for class/interface info
   * @throws TypeHierarchyException
   */
  private void addNode(CompilationUnit compilationUnit) throws TypeHierarchyException, DeadCodeException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    if (classOrInterface.getTokenType().equals(TokenType.ClassDeclaration) ||
      classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration)) {
      graph.addNode(classOrInterface);
    } else {
      throw new DeadCodeException("Expecting a ClassDeclaration or InterfaceDeclaration token but received " + classOrInterface.getTokenType());
    }
  }

  /**
   * Perform all the necessary hierarchy verifications
   * Please see class comments for full detail
   * @throws TypeHierarchyException
   */
  private void verifyHierarchyGraph() throws TypeHierarchyException {
    HierarchyGraphNode parentNode;
    HierarchyGraphNode currentNode;
    String name;

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      name = entry.getKey();
      currentNode = entry.getValue();

      extendsVerification(currentNode.extendsList, currentNode);
      implementsVerification(currentNode.implementsList, name);
    }
    verifyHierarchyGraphIsAcyclic();
    verifyMethodHierarchy();
  }

  /*******************************************  Verification Functions *******************************************/

  /**
   * Perform verification on the implements clause of a class
   * @param implementedParents interfaces implemented by this class
   * @param className name of the class being processed
   * @throws TypeHierarchyException
   */
  private void implementsVerification(List<HierarchyGraphNode> implementedParents, String className) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : implementedParents) {
      if (parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("A Class cannot implement a class [class: " + className +
          ", implemented class: " + parent.identifier + "]");
      }
    }
  }

  /**
   * Perform verification on the extends clause of a class or interface
   * @param parents parents of the class being processed
   * @param currentNode HierarchyGraph node associated to the class/interface being processed
   * @throws TypeHierarchyException
   */
  public void extendsVerification(List<HierarchyGraphNode> parents, HierarchyGraphNode currentNode) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : parents) {
      if (currentNode.classOrInterface instanceof ClassDeclaration) {
        if (parent.classOrInterface instanceof InterfaceDeclaration) {
          throw new TypeHierarchyException("A class cannot extend an interface[class: " + currentNode.identifier +
            ", interface: " + parent.identifier + "]");
        }
        if (parent.isFinal()) {
          throw new TypeHierarchyException("Class " + currentNode.identifier + " is extending final class " + parent.identifier);
        }
      } else if (currentNode.classOrInterface instanceof InterfaceDeclaration &&
        parent.classOrInterface instanceof ClassDeclaration) {
        throw new TypeHierarchyException("An interface cannot extend a class[Interface " + currentNode.identifier +
          ", class:" + parent.identifier + "]");
      }
    }
  }

  private void verifyHierarchyGraphIsAcyclic() throws TypeHierarchyException {
    HierarchyGraphNode cyclicNode;
    if ((cyclicNode = isCyclic()) != null) throw new TypeHierarchyException("Graph is not acyclic.  " +
      cyclicNode.identifier + " is causing cycles in hierarchy checking");
  }

  public HierarchyGraphNode verifyMethodHierarchy() throws TypeHierarchyException {
    //Skip verifying nodes that have already been verified
    HashSet<HierarchyGraphNode> verified = new HashSet<HierarchyGraphNode>();

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      HierarchyGraphNode currentNode = entry.getValue();
      if (!verified.contains(currentNode)) {
        verifyOwnedMethods(currentNode);
      }
      verifyExtendedMethods(currentNode, verified);
    }
    return null;
  }

  private List<Method> verifyExtendedMethods(HierarchyGraphNode currentNode, HashSet<HierarchyGraphNode> verified) throws TypeHierarchyException {
    List<Method> extendedMethods = new ArrayList<Method>();
    // Use depth first to start from the bottom of the hierarchy tree
    // and work our way up.
    for (HierarchyGraphNode node : currentNode.extendsList) {
      extendedMethods.addAll(verifyExtendedMethods(node, verified));
    }
    if (!verified.contains(currentNode)) {
      extendedMethodChecks(currentNode, extendedMethods);
      verified.add(currentNode);
    };
    extendedMethods.addAll(currentNode.methods);
    return extendedMethods;
  }

  private void extendedMethodChecks(HierarchyGraphNode currentNode, List<Method> extendedMethods) throws TypeHierarchyException {
    for (Method extendedMethod : extendedMethods) {
      for (Method method : currentNode.methods) {
        if (extendedMethod.signaturesMatch(method)) {
          if (!extendedMethod.returnType.equals(method.returnType)) {
            throw new TypeHierarchyException("A class or interface must not contain two methods with the same signature but different return types");
          }
          if (extendedMethod.isStatic() && !method.isStatic()) {
            throw new TypeHierarchyException("A nonstatic method must not replace a static method");
          }
          if (extendedMethod.isPublic() && method.isProtected()) {
            throw new TypeHierarchyException("A protected method must not replace a public method.");
          }
          if (extendedMethod.isFinal()) {
            throw new TypeHierarchyException("A method must not replace a final method.");
          }
        }
      }
    }
  }

  private void verifyOwnedMethods(HierarchyGraphNode currentNode) throws TypeHierarchyException {
    boolean classIsStatic = currentNode.modifiers.contains(TokenType.ABSTRACT);
    for (int i = 0; i < currentNode.methods.size(); i++) {
      for (int j = 0; j < currentNode.methods.size(); j++) {
        if (i != j && currentNode.methods.get(i).signaturesMatch(currentNode.methods.get(j))) {
          throw new TypeHierarchyException("A method with the exact same signature is found in "
            + currentNode.identifier + " Method: " + currentNode.methods.get(i));
        }
      }
      if (!classIsStatic && currentNode.methods.get(i).isAbstract()) {
        throw new TypeHierarchyException(currentNode.identifier + " declares an abstract method but the class is not abstract.");
      }
    }
  }

  private HierarchyGraphNode isCyclic() {
    HashSet<HierarchyGraphNode> visited = new HashSet<HierarchyGraphNode>();
    HashSet<HierarchyGraphNode> recursionStack = new HashSet<HierarchyGraphNode>();
    HierarchyGraphNode cyclicNode;

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      HierarchyGraphNode currentNode = entry.getValue();
      if ((cyclicNode = isCyclicHelper(currentNode, visited, recursionStack)) != null) {
        return cyclicNode;
      }
    }
    return null;
  }

  private HierarchyGraphNode isCyclicHelper(HierarchyGraphNode currentNode, HashSet<HierarchyGraphNode> visited, HashSet<HierarchyGraphNode> recursionStack) {
    if (!visited.contains(currentNode)) {
      visited.add(currentNode);
      recursionStack.add(currentNode);
      for (HierarchyGraphNode child : currentNode.children) {
        if ( (!visited.contains(child) && isCyclicHelper(child, visited, recursionStack) != null) ||
          recursionStack.contains(child)) {
          return child;
        }
      }
    }
    recursionStack.remove(currentNode);
    return null;
  }
}
