package rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class OveruseIfElse extends AbstractJavaRule {

	public static final int MAX_ALLOWED_IF_ELSE = 3;

	@Override
	public Object visit(ASTIfStatement node, Object data){
		String lastVar = "";
		if(hasElseWithBool(node)){
			ASTName nameNode = node.getFirstDescendantOfType(ASTName.class);
			lastVar = nameNode.getImage();
			ASTIfStatement tempNode = null;
			for(int i = 0; i < node.jjtGetNumChildren() && tempNode == null; i++){ //travel over the children to see which one is the next if statement.
				tempNode = node.jjtGetChild(i).getFirstChildOfType(ASTIfStatement.class);
			}
			//first loop is done above => i = 1, the rest should be done up to MAX_ALLOWED_IF_ELSE - 1 (as it is exlusive).
			for(int i = 2; i <= MAX_ALLOWED_IF_ELSE; i++){
				if(hasElseWithBool(tempNode)){
					nameNode = tempNode.getFirstDescendantOfType(ASTName.class);
					if(nameNode == null){
						break;
					}
					String newVar = nameNode.getImage();
					if(!newVar.equals(lastVar)){ //equality check is done on another variable, therefore switch suggestion is useless.
						break;
					}else if(i == MAX_ALLOWED_IF_ELSE){ //maximum amount of checks on the same variable is reached
						addViolation(data, node);
					}else{
						lastVar = newVar;
						ASTIfStatement helpNode = null;
						for(int j = 0; j < tempNode.jjtGetNumChildren() && helpNode == null; j++){ //travel over the children to see which one is the next if statement.
							helpNode = tempNode.jjtGetChild(i).getFirstChildOfType(ASTIfStatement.class);
						}
						tempNode = helpNode;
					}
				}
			}
		}
		return super.visit(node, data);
	}

	public boolean hasElseWithBool(ASTIfStatement node){
		boolean res = false;
		if(node != null && node.hasElse()){
			ASTExpression expr = (ASTExpression) node.jjtGetChild(0);
			Node child = expr.jjtGetChild(0);
			if(child instanceof ASTEqualityExpression){
				res = true;
			}
		}
		return res;
	}
}
