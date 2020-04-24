package ast;

import visitors.NodeVisitor;

/**
 * Represents '! x' expression where 'x' is boolean
 * 
 * @author Paolo Baldini
 */
public class NotNode implements Node {

	private Node exp;	// the boolean expression

	public NotNode( Node exp ) {
		this.exp = exp;
	}

	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
