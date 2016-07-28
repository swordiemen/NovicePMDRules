package rules;

import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class NonStaticAsStatic extends AbstractJavaRule {
	@Override
	public Object visit(ASTPrimaryPrefix node, Object data) {
		return super.visit(node, data);
	}
}
