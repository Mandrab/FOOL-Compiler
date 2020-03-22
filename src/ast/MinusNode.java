package ast;

import visitors.NodeVisitor;

public class MinusNode implements Node {

	private Node left;
	private Node right;

	public MinusNode( Node left, Node right ) {
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
	
	
	
	



	
	


	@Override
	public String codeGeneration() {
		return left.codeGeneration() + right.codeGeneration() + "sub\n";
	}

}
