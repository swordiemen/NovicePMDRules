package rules;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ConfusingEqualsWithAssignment extends AbstractJavaRule {
	public static final int DISTANCE_FROM_ASSIGNMENT_TO_STATEMENT = 2;
	
	public Object visit(ASTAssignmentOperator node, Object data){
		RuleContext rc = (RuleContext) data;
		if(rc.getSourceCodeFilename().contains("Test.java")){
			System.out.println("");
		}
		//check where this assignment is made
		Node parent = node.getNthParent(DISTANCE_FROM_ASSIGNMENT_TO_STATEMENT);
		if(parent != null && isConditionalStat(parent)){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}
	
	public boolean isConditionalStat(Node node){
		return (node instanceof ASTIfStatement) || (node instanceof ASTWhileStatement) || (node instanceof ASTForStatement);
	}
}
