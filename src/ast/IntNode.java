package ast;

import visitors.NodeVisitor;

/**
 * Represents an integer value
 * 
 * @author Paolo Baldini
 */
public class IntNode implements Node {

	private Integer value;

	public IntNode( Integer value ) {
		this.value = value;
	}
	
	public int getValue( ) {
		return value;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}