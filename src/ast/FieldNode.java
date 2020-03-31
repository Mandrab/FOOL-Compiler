package ast;

import visitors.NodeVisitor;

public class FieldNode implements DecNode, Node {

	private String ID;
	private Node type;
	private int offset;

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
	
	public int getOffset() {
		return this.offset;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
