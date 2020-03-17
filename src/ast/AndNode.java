package ast;

import lib.TypeException;

public class AndNode implements Node {

	private Node left;
	private Node right;

	public AndNode (Node l, Node r) {
		left=l;
		right=r;
	}

	public String toPrint(String s) {
		return s+"And\n" + left.toPrint(s+"  ")  
		+ right.toPrint(s+"  ") ; 
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
