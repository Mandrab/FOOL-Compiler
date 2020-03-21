package ast;

import visitors.Visitor;

public class BoolNode implements Node {

	private boolean value;

	public BoolNode( boolean value ) {
		this.value = value;
	}
	
	public boolean getValue( ) {
		return value;
	}

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	

	public Node typeCheck() {
		return new BoolTypeNode();
	}

	public String codeGeneration() {
		return "push " + (value ? 1 : 0) + "\n";
	}

}