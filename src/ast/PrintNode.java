package ast;

import lib.*;
import visitors.Visitor;

public class PrintNode implements Node {

	private Node exp;

	public PrintNode( Node expression ) {
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
		return exp.codeGeneration() + "print\n";
	}
}