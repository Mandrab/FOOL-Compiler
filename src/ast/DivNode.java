package ast;

import visitors.NodeVisitor;

public class DivNode implements Node {

	private Node left;
	private Node right;

	public DivNode( Node left, Node right ) {
		this.left = left;
		this.right = right;
	}
	
	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	





	

	public String codeGeneration() {
		return left.codeGeneration() + right.codeGeneration() + "div\n";
	}

}
