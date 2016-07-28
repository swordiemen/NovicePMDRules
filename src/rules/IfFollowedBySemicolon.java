package rules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class IfFollowedBySemicolon extends AbstractJavaRule {
	private static final String EMPTY_STATEMENT = "EmptyStatement";

	@Override
	public Object visit(ASTIfStatement node, Object data){
		if(hasEmptyStatement(node) && hasSemiColonOnLine(node, (RuleContext) data)){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}

	public boolean hasEmptyStatement(ASTIfStatement node){
		return node.getFirstChildOfType(ASTStatement.class).jjtGetChild(0).toString().equals(EMPTY_STATEMENT);
	}

	public boolean hasSemiColonOnLine(Node node, RuleContext rc){
		int line = node.getBeginLine();
		//can't access source code from within, have to read the file manually
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(rc.getSourceCodeFile()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String code = "";
		try{
			for(int i = 0; i < line; i++){
				code = br.readLine();
			}
			br.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		return code.contains(";");
	}
}
