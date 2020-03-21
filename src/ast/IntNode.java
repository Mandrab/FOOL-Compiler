package ast;

import visitors.Visitor;

public class IntNode implements Node {

	private Integer value;

	public IntNode( Integer value ) {
		this.value = value;
	}
	
	public int getValue( ) {
		return value;
	}
	
	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	



	public Node typeCheck() {
		return new IntTypeNode();
	}

	public String codeGeneration() {
		return "push " + value + "\n";
	}

}