package ast;

import visitors.NodeVisitor;

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
	public <T> T accept(NodeVisitor<T> visitor) {
		return visitor.visit( this );
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
