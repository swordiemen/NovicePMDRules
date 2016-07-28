package rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class GlobalVarCanBeLocal extends AbstractJavaRule {
	private HashMap<String, ArrayList<String>> varMap = new HashMap<String, ArrayList<String>>(); 


	public void work(Node node, Object data){
		if((node instanceof ASTPrimaryPrefix || node instanceof ASTPrimarySuffix)){
			String varName = "";
			if(node.jjtGetNumChildren() > 0){ //prefixes use ASTNames
				varName = node.jjtGetChild(0).getImage();
			}else if(node.getImage() != null){ //suffixes just have it in their own node
				varName = node.getImage();
			}else{
				return;
			}
			String methodName = null;
			if(isInMethod(node)){
				methodName = getMethodName(node);
			}else if(isInConstructor(node)){
				methodName = "";
			}else{
				return;
			}
			HashMap<String, ArrayList<String>> varMapCopy = new HashMap<>();
			for(String key : varMap.keySet()){
				varMapCopy.put(key, varMap.get(key));
			}
			//Capital letter start indicates a constant
			for(String knownVar : varMapCopy.keySet()){
				if(knownVar == null || varName == null){
					return;
				}
				if((knownVar.equals(varName) || varName.contains(knownVar+".")) && !varMap.get(knownVar).contains(methodName)){
					ArrayList<String> varMethods = varMap.get(knownVar);
					varMethods.add(methodName);
					varMap.put(knownVar, varMethods);
				}
			}
		}
	}

	private boolean isInConstructor(Node node) {
		return node.getFirstParentOfType(ASTConstructorDeclaration.class) != null;
	}

	private boolean isInMethod(Node node) {
		return node.getFirstParentOfType(ASTMethodDeclaration.class) != null;
	}

	public String getMethodName(Node node) {
		ASTMethodDeclaration methodNode = node.getFirstParentOfType(ASTMethodDeclaration.class);
		return methodNode.getMethodName();
	}

	@Override
	public Object visit(ASTCompilationUnit node, Object data){
		varMap.clear();
		HashMap<String, ASTFieldDeclaration> varNodes = new HashMap<String, ASTFieldDeclaration>();
		List<ASTFieldDeclaration> vars = node.findDescendantsOfType(ASTFieldDeclaration.class);
		for(ASTFieldDeclaration var : vars){
			if(!var.isFinal() && Character.isLowerCase(var.getVariableName().charAt(0))){
				String varName = var.getVariableName();
				varMap.put(varName, new ArrayList<String>());
				varNodes.put(varName, var);
			}
		}
		List<ASTPrimaryPrefix> priPres = node.findDescendantsOfType(ASTPrimaryPrefix.class);
		for(ASTPrimaryPrefix priPre : priPres){
			work(priPre, data);
		}
		List<ASTPrimarySuffix> priSufs = node.findDescendantsOfType(ASTPrimarySuffix.class);
		for(ASTPrimarySuffix priSuf : priSufs){
			work(priSuf, data);
		}
		for(String var : varMap.keySet()){
			if(varMap.get(var).size() == 1){//0 methods => most likely a constant, 1 method => can be used as a local var, >1 methods => correctly labeled global variable
				System.out.println(var);
				addViolation(data, varNodes.get(var));
			}
		}
		return super.visit(node, data);
	}

}
