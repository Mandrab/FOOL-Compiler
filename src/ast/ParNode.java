package ast;

import visitors.NodeVisitor;

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
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	


	
	



	// non utilizzato
	public String codeGeneration() {
		return "";
	}

	

	

}