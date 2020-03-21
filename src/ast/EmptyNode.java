package ast;

import visitors.Visitor;

public class EmptyNode implements Node {

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}


	
	

	public Node typeCheck() {
		return new EmptyTypeNode(); 
	}
	/*Mette -1 sullo stack, come scritto nelle slide. Questo perch� -1 � diverso da qualsiasi indirizzo
	 * dello stack. */
	public String codeGeneration() {
		return "push -1\n";
	}
}
