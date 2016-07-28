package rules;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class NullCheckFollowedByOr extends AbstractJavaRule {
	public static final String NOT_EQUALS = "!=";
	public static final String NULL_NAME = "NullLiteral";
	private String nullCheck = "";

	@Override
	public Object visit(ASTConditionalOrExpression node, Object data){
		nullCheck = "";
		if(isNullCheck(node.jjtGetChild(0)) && nextOneUsesSaidCheck(node.jjtGetChild(1))){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}

	public boolean nextOneUsesSaidCheck(Node node) {
		boolean result = false;
		ASTPrimaryPrefix priPre = node.getFirstChildOfType(ASTPrimaryPrefix.class);
		List<ASTPrimarySuffix> priSufs = node.findDescendantsOfType(ASTPrimarySuffix.class);
		if(!nullCheck.equals("") && priPre != null){
			result = priPre.jjtGetChild(0).getImage().contains(nullCheck);
			for(ASTPrimarySuffix priSuf : priSufs){
				if(priSuf.getImage() != null && priSuf.getImage().contains(nullCheck)){
					result = true;
				}
			}
		}
		return result;
	}

	public boolean isNullCheck(Node node){
		boolean isNotEqualExpr = (node instanceof ASTEqualityExpression && ((ASTEqualityExpression) node).getImage().equals(NOT_EQUALS));
		if(!isNotEqualExpr){
			return false;
		}
		boolean isNull = false;
		if(isNull(node.jjtGetChild(0))){
			nullCheck = node.jjtGetChild(1).jjtGetChild(0).getImage();
			isNull = true;
		}else if(isNull(node.jjtGetChild(1))){
			nullCheck = node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).getImage();
			isNull = true;
		}
		return isNotEqualExpr && isNull;
	}

	public boolean isNull(Node node){
		boolean result = false;
		if(node instanceof ASTPrimaryExpression){
			ASTPrimaryExpression prExpr = (ASTPrimaryExpression) node;
			ASTPrimaryPrefix prPre = (ASTPrimaryPrefix) prExpr.jjtGetChild(0);
			if(prPre.jjtGetNumChildren() > 0 && prPre.jjtGetChild(0) instanceof ASTLiteral){
				ASTLiteral lit = (ASTLiteral) prPre.jjtGetChild(0);
				if(lit.jjtGetNumChildren() > 0){
					result = lit.jjtGetChild(0).toString().equals(NULL_NAME);
				}else{
					result = lit.getImage().equals(NULL_NAME);
				}
			}
		}
		return result;
	}
}
