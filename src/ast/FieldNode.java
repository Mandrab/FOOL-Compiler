package ast;

import visitors.NodeVisitor;

/**
 * Represents a field
 * 
 * @author Paolo Baldini
 */
public class FieldNode implements DecNode, Node {

	private String ID;	// field ID
	private Node type;	// field's type
	private int offset;	// field's offset in the class declaration

	public FieldNode( String id, Node type, int offset ) {
		this.ID = id;
		this.type = type;
		this.offset = offset;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public Node getSymType( ) {
		return type;
	}
	
	public void setOffset( int offset ) {
		this.offset = offset;
	}
	
	public int getOffset( ) {
		return this.offset;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
