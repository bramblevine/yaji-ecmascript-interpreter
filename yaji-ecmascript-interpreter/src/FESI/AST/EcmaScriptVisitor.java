/* Generated By:JJTree: Do not edit this line. EcmaScriptVisitor.java */

package FESI.AST;

public interface EcmaScriptVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTLiteral node, Object data);
  public Object visit(ASTIdentifier node, Object data);
  public Object visit(ASTThisReference node, Object data);
  public Object visit(ASTCompositeReference node, Object data);
  public Object visit(ASTFunctionCallParameters node, Object data);
  public Object visit(ASTPropertyValueReference node, Object data);
  public Object visit(ASTPropertyIdentifierReference node, Object data);
  public Object visit(ASTAllocationExpression node, Object data);
  public Object visit(ASTOperator node, Object data);
  public Object visit(ASTPostfixExpression node, Object data);
  public Object visit(ASTUnaryExpression node, Object data);
  public Object visit(ASTBinaryExpressionSequence node, Object data);
  public Object visit(ASTAndExpressionSequence node, Object data);
  public Object visit(ASTOrExpressionSequence node, Object data);
  public Object visit(ASTConditionalExpression node, Object data);
  public Object visit(ASTAssignmentExpression node, Object data);
  public Object visit(ASTExpressionList node, Object data);
  public Object visit(ASTStatement node, Object data);
  public Object visit(ASTStatementList node, Object data);
  public Object visit(ASTVariableDeclaration node, Object data);
  public Object visit(ASTIfStatement node, Object data);
  public Object visit(ASTWhileStatement node, Object data);
  public Object visit(ASTForStatement node, Object data);
  public Object visit(ASTEmptyExpression node, Object data);
  public Object visit(ASTForVarStatement node, Object data);
  public Object visit(ASTForInStatement node, Object data);
  public Object visit(ASTForVarInStatement node, Object data);
  public Object visit(ASTContinueStatement node, Object data);
  public Object visit(ASTBreakStatement node, Object data);
  public Object visit(ASTReturnStatement node, Object data);
  public Object visit(ASTWithStatement node, Object data);
  public Object visit(ASTFunctionDeclaration node, Object data);
  public Object visit(ASTFormalParameterList node, Object data);
  public Object visit(ASTProgram node, Object data);
  public Object visit(ASTSuperReference node, Object data);
}
