package ast;

import visitors.NodeVisitor;

public class VarNode implements Node, DecNode {

	private String ID;
	private Node type;
	private Node exp;

	public VarNode( String id, Node type, Node expression ) {
		this.ID = id;
		this.type = type;
		this.exp = expression;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public Node getSymType( ) {
		return type;
	}
	
	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}