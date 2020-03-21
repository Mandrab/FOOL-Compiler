package ast;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

public class AndNode implements Node {

	private Node left;
	private Node right;

	public AndNode ( Node left, Node right ) {
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
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit( this );
	}
	
	
	
	
	
	/*Da riguardare, � possibile l'and solo tra booleani.*/
	public Node typeCheck() throws TypeException {
		Node l= left.typeCheck();  
		Node r= right.typeCheck();  
		if (!(l instanceof BoolTypeNode && r instanceof BoolTypeNode))
			throw new TypeException("Incompatible types in and");
		return new BoolTypeNode();
	}
	
	/* Questo metodo restituisce il risultato della moltiplicazione tra i due elementi in input. Restituisce:
	 * 1 per True
	 * 0 per False.  */
	public String codeGeneration() {
		  return left.codeGeneration()+
				 right.codeGeneration()+
				 "mult\n";	   
	}

}
