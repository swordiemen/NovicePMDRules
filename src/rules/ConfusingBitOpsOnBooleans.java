package rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ConfusingBitOpsOnBooleans extends AbstractJavaRule {
	
	@Override
	public Object visit(ASTAndExpression node, Object data){
		boolean expr1bool = isBoolNode(node.jjtGetChild(0));
		boolean expr2bool = isBoolNode(node.jjtGetChild(1));
		if(expr1bool && expr2bool){
			addViolation(data, node);
		}
		return super.visit(node,data);	
	}
	
	public boolean isBoolNode(Node node){
		boolean result = false;
		if(node instanceof ASTRelationalExpression){
			result = true;
		}else if(node instanceof ASTExpression){
			ASTExpression ae = (ASTExpression) node;
			result = ae.getType().getName().equals("boolean");
		}else if(node instanceof AbstractJavaTypeNode){
			AbstractJavaTypeNode ae = (AbstractJavaTypeNode) node;
			result = ae.getType().getName().equals("boolean");
		}
		return result;
	}
}
