package ast;

import visitors.NodeVisitor;

/**
 * Represents a function parameter
 * 
 * @author Paolo Baldini
 */
public class ParNode implements Node, DecNode {

	private String ID;	// ID of parameter
	private Node type;	// type of parameter

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

}