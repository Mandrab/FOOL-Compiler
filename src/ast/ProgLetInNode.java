package ast;

import lib.*;
import visitors.NodeVisitor;

import java.util.List;

public class ProgLetInNode implements Node {

	private List<Node> declarations;
	private Node exp;

	public ProgLetInNode( List<Node> declarations, Node expression ) {
		this.declarations = declarations;
		this.exp = expression;
	}
	
	public List<Node> getDeclarations( ) {
		return declarations;
	}

	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	


	
	
	


	public String codeGeneration() {
		String declCode = "";
		for (Node dec : declarations)
			declCode += dec.codeGeneration();
		return "push 0\n" + declCode + exp.codeGeneration() + "halt\n" + FOOLlib.getCode();
	}
}