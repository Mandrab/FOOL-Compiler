package ast;

import visitors.Visitor;

public class FieldNode implements DecNode, Node {

	private String ID;
	private Node type;
	private int offset;

	public FieldNode( String id, Node type ) {
		this.ID = id;
		this.type = type;
		this.offset = 0;
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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	


	// non utilizzato
	public Node typeCheck() {
		return null;
	}

	// non utilizzato
	public String codeGeneration() {
		return "";
	}
	
	

}
