package ast;

import lib.*;
import visitors.Visitor;

public class TimesNode implements Node {

	private Node left;
	private Node right;

	public TimesNode( Node left, Node right ) {
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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	



	

	public Node typeCheck() throws TypeException {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode())))
			throw new TypeException("Non integers in multiplication");
		return new IntTypeNode();
	}

	public String codeGeneration() {
		return left.codeGeneration() + right.codeGeneration() + "mult\n";
	}

}