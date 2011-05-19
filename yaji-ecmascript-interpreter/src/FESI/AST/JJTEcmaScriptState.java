/* Generated By:JJTree: Do not edit this line. JJTEcmaScriptState.java */

package FESI.AST;


/**
 *              The JJTEcmaScriptState object implements the java.io.Serializable
 */
public class JJTEcmaScriptState implements java.io.Serializable {
    private static final long serialVersionUID = -1384341816423710837L;
private final java.util.Stack nodes;
  private final java.util.Stack marks;

  private int sp;		// number of nodes on stack
  private int mk;		// current mark
  private boolean node_created;

  public JJTEcmaScriptState() {
    nodes = new java.util.Stack();
    marks = new java.util.Stack();
    sp = 0;
    mk = 0;
  }

  /* Determines whether the current node was actually closed and
     pushed.  This should only be called in the final user action of a
     node scope.  */
  public boolean nodeCreated() {
    return node_created;
  }

  /* Call this to reinitialize the node stack.  It is called
     automatically by the parser's ReInit() method. */
  public void reset() {
    nodes.removeAllElements();
    marks.removeAllElements();
    sp = 0;
    mk = 0;
  }

  /* Returns the root node of the AST.  It only makes sense to call
     this after a successful parse. */
  public Node rootNode() {
    return (Node)nodes.elementAt(0);
  }

  /* Pushes a node on to the stack. */
  public void pushNode(Node n) {
    nodes.push(n);
    ++sp;
  }

  /* Returns the node on the top of the stack, and remove it from the
     stack.  */
  public Node popNode() {
    if (--sp < mk) {
      mk = ((Integer)marks.pop()).intValue();
    }
    return (Node)nodes.pop();
  }

  /* Returns the node currently on the top of the stack. */
  public Node peekNode() {
    return (Node)nodes.peek();
  }

  /* Returns the number of children on the stack in the current node
     scope. */
  public int nodeArity() {
    return sp - mk;
  }


  public void clearNodeScope(Node n) {
    while (sp > mk) {
      popNode();
    }
    mk = ((Integer)marks.pop()).intValue();
  }


  public void openNodeScope(Node n) {
    marks.push(Integer.valueOf(mk));
    mk = sp;
    n.jjtOpen();
  }


  /* A definite node is constructed from a specified number of
     children.  That number of nodes are popped from the stack and
     made the children of the definite node.  Then the definite node
     is pushed on to the stack. */
  public void closeNodeScope(Node n, int num) {
    mk = ((Integer)marks.pop()).intValue();
    while (num-- > 0) {
      Node c = popNode();
      c.jjtSetParent(n);
      n.jjtAddChild(c, num);
    }
    n.jjtClose();
    pushNode(n);
    node_created = true;
  }


  /* A conditional node is constructed if its condition is true.  All
     the nodes that have been pushed since the node was opened are
     made children of the the conditional node, which is then pushed
     on to the stack.  If the condition is false the node is not
     constructed and they are left on the stack. */
  public void closeNodeScope(Node n, boolean condition) {
    if (condition) {
      int a = nodeArity();
      mk = ((Integer)marks.pop()).intValue();
      while (a-- > 0) {
	Node c = popNode();
	c.jjtSetParent(n);
	n.jjtAddChild(c, a);
      }
      n.jjtClose();
      pushNode(n);
      node_created = true;
    } else {
      mk = ((Integer)marks.pop()).intValue();
      node_created = false;
    }
  }
}
