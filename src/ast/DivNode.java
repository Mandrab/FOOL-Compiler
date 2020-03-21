package ast;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	



	public Node typeCheck() throws TypeException {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode())))
			throw new TypeException("Non integers in division");
		return new IntTypeNode();
	}

	public String codeGeneration() {
		return left.codeGeneration() + right.codeGeneration() + "div\n";
	}

}
