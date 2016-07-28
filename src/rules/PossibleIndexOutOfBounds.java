package rules;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class PossibleIndexOutOfBounds extends AbstractJavaRule{
	public static final String LE = "<=";
	public static final String SIZE_CHECK = "size";
	public static final String LENGTH_CHECK = "length";

	@Override
	public Object visit(ASTWhileStatement node, Object data){
		ASTRelationalExpression relExpr = node.getFirstDescendantOfType(ASTRelationalExpression.class);
		if(relExpr != null && relExpr.getImage().equals(LE) && hasCheckOnSize(relExpr)){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTForStatement node, Object data){
		ASTRelationalExpression relExpr = node.getFirstDescendantOfType(ASTRelationalExpression.class);
		if(((RuleContext) data).getSourceCodeFilename().contains("Strings.java")){
			System.out.println("a");
		}
		if(relExpr != null && relExpr.getImage().equals(LE) && hasCheckOnSize(relExpr)){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}

	private boolean hasCheckOnSize(Node relExpr) {
		boolean res = false;
		if(relExpr.jjtGetChild(1) instanceof ASTPrimaryExpression){
			ASTPrimaryExpression priExpr = (ASTPrimaryExpression) relExpr.jjtGetChild(1);
			List<ASTPrimarySuffix> sufList = priExpr.findChildrenOfType(ASTPrimarySuffix.class);
			String lastCalledMethod;
			if(sufList.size() == 0){
				String totalName = priExpr.jjtGetChild(0).jjtGetChild(0).getImage();
				String[] name = null;
				if(totalName != null){
					name = totalName.split("\\.");
				}else{
					return false;
				}
				lastCalledMethod = name.length == 0 ? "" : name[name.length - 1];
			}else if(sufList.size() == 1){
				String[] name = priExpr.jjtGetChild(0).jjtGetChild(0).getImage().split("\\.");
				lastCalledMethod = name[name.length - 1];
			}else{
				Node node1 = sufList.get(sufList.size() - 2);
				String[] name = node1.getImage().split("\\.");
				lastCalledMethod = name[name.length - 1];
			}
			if(lastCalledMethod.equals(SIZE_CHECK) || lastCalledMethod.equals(LENGTH_CHECK)){
				res = true;
			}
		}

		return res;
	}
}
