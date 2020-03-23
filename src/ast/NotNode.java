package ast;

import visitors.NodeVisitor;

public class NotNode implements Node {

	private Node exp;

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
