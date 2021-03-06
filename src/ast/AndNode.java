package ast;

import visitors.NodeVisitor;

/**
 * Represents 'x && y' expression
 * 
 * @author Paolo Baldini
 */
public class AndNode implements Node {

	private Node left;
	private Node right;

	public AndNode ( Node left, Node right ) {
		this.left = left;
		this.right = right;
	}

	public Node getLeft( ) {
		return left;
	}

	public Node getRight( ) {
		return right;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
