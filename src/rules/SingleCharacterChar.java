package rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SingleCharacterChar extends AbstractJavaRule{
	
	@Override
	public Object visit(ASTLocalVariableDeclaration node, Object data){
		ASTType typeNode = node.getTypeNode();
		Node childNode = typeNode.jjtGetChild(0);
		if(childNode.getImage() != null && childNode.getImage().equals("char") && node.getVariableName().length() == 1){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}
	
	@Override
	public Object visit(ASTFieldDeclaration node, Object data){
		Class<?> typeNode = node.getType();
		if(typeNode != null && typeNode.getName() != null && typeNode.getName().equals("char") && node.getVariableName().length() == 1){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}
	
}
