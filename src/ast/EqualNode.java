package ast;

import lib.*;
import visitors.Visitor;

public class EqualNode implements Node {

	private Node left;
	private Node right;

	public EqualNode( Node left, Node right ) {
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
		Node l = left.typeCheck();
		Node r = right.typeCheck();
		if (r instanceof ArrowTypeNode || l instanceof ArrowTypeNode) {
			throw new TypeException("Incompatible types in equal");
		}
		if (!(FOOLlib.isSubtype(l, r) || FOOLlib.isSubtype(r, l)))
			throw new TypeException("Incompatible types in equal");
		return new BoolTypeNode();
	}

	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return left.codeGeneration() + right.codeGeneration() + "beq " + l1 + "\n" + "push 0\n" + "b " + l2 + "\n" + l1
				+ ": \n" + "push 1\n" + l2 + ": \n";
	}

}