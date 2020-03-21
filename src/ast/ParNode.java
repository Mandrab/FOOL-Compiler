package ast;

import visitors.Visitor;

public class ParNode implements Node, DecNode {

	private String ID;
	private Node type;

	public ParNode( String id, Node type ) {
		this.ID = id;
		this.type = type;
	}
	
	public String getID( ) {
		return ID;
	}

	public Node getSymType( ) {
		return type;
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