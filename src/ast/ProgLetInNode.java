package ast;

import lib.*;
import visitors.Visitor;

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

	public Node getExp( ) {
		return exp;
	}

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	


	
	
	

	public Node typeCheck() throws TypeException {
		for (Node dec : declarations)
			try {
				dec.typeCheck();
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		return exp.typeCheck();
	}

	public String codeGeneration() {
		String declCode = "";
		for (Node dec : declarations)
			declCode += dec.codeGeneration();
		return "push 0\n" + declCode + exp.codeGeneration() + "halt\n" + FOOLlib.getCode();
	}
}