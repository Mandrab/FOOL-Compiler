package ast;

import lib.*;
import visitors.Visitor;

public class GreaterEqualNode implements Node {

	private Node left;
	private Node right;

	public GreaterEqualNode( Node left, Node right ) {
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
		if (!(FOOLlib.isSubtype(l, r) || FOOLlib.isSubtype(r, l)))
			throw new TypeException("Incompatible types in  greaterequal");
		return new BoolTypeNode();
	}

	/*
	 * Per la code generation uso comunque ble (branch less equal) che effettua il
	 * salto se il primo valore � minore uguale al secondo. Poi adatto le condizioni
	 * del salto, ragionando in maniera inversa.
	 */
	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return left.codeGeneration() + right.codeGeneration() + "stm\n" + "lhp\n" + "sw\n" + "ltm\n" + "lhp\n" + "lw\n"
				+ "bleq " + l1 + "\n" + "push 0\n" + // in caso negativo pusho 0 (false)
				"b " + l2 + "\n" + l1 + ": \n" + "push 1\n" + // in caso positivo pusho 1 (true)
				l2 + ": \n";
	}

}