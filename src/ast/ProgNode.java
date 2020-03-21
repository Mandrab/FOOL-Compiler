package ast;

import lib.*;
import visitors.Visitor;

public class ProgNode implements Node {

	private Node exp;

	public ProgNode( Node expression ) {
		exp = expression;
	}
	
	public Node getExp( ) {
		return exp;
	}

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	



	

	public Node typeCheck() throws TypeException {
		return exp.typeCheck();
	}

	public String codeGeneration() {
		return exp.codeGeneration() + "halt\n";
	}
}