package ast;

import visitors.NodeVisitor;

/**
 * A type that represents a reference to a class (i.e., type of variable is a class)
 * 
 * @author Paolo Baldini
 */
public class RefTypeNode implements Node {

	private String ID;

	public RefTypeNode( String ID ) {
		this.ID = ID;
	}

	public String getID( ) {
		return ID;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
