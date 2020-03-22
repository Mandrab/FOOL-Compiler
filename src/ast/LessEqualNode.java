package ast;

import lib.*;
import visitors.NodeVisitor;

public class LessEqualNode implements Node {

	private Node left;
	private Node right;

	public LessEqualNode( Node left, Node right ) {
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
	
	
	
	
	
	
	






	// Per la code generation uso ble (branch less equal) che effettua il salto se
	// il primo valore è minore uguale al secondo.
	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return left.codeGeneration() + right.codeGeneration() + "bleq " + l1 + "\n" + "push 0\n" + // in caso negativo
																									// pusho 0 (false)
				"b " + l2 + "\n" + l1 + ": \n" + "push 1\n" + // in caso positivo pusho 1 (true)
				l2 + ": \n";
	}

}