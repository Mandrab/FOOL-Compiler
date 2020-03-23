package ast;

import visitors.NodeVisitor;

public class IfNode implements Node {

	private Node condition;
	private Node thenBranch;
	private Node elseBranch;

	public IfNode( Node condition, Node thenBranch, Node elseBranch ) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
	
	public Node getCondition( ) {
		return condition;
	}

	public Node getThenBranch( ) {
		return thenBranch;
	}

	public Node getElseBranch( ) {
		return elseBranch;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}