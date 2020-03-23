package ast;

import visitors.NodeVisitor;

public class BoolNode implements Node {

	private boolean value;

	public BoolNode( boolean value ) {
		this.value = value;
	}
	
	public boolean getValue( ) {
		return value;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}