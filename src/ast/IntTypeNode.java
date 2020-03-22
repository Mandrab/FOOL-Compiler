package ast;

import visitors.NodeVisitor;

public class IntTypeNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	


	
	
	




	// non utilizzato
	public String codeGeneration() {
		return "";
	}

}