package ast;

import visitors.NodeVisitor;

/**
 * Represents a program without declarations (only main expression)
 * 
 * @author Paolo Baldini
 */
public class ProgNode implements Node {

	private Node exp;

	public ProgNode( Node expression ) {
		exp = expression;
	}

	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}