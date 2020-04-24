package ast;

import visitors.NodeVisitor;

/**
 * Represents a variable in the AST
 * 
 * @author Paolo Baldini
 */
public class VarNode implements Node, DecNode {

	private String ID;	// variable's name
	private Node type;	// type of variable
	private Node exp;	// expression (value) of the variable

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