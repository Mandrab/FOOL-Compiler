package ast;

import visitors.NodeVisitor;

public class EmptyNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}


	
	



	
	/*Mette -1 sullo stack, come scritto nelle slide. Questo perch� -1 � diverso da qualsiasi indirizzo
	 * dello stack. */
	public String codeGeneration() {
		return "push -1\n";
	}
}
