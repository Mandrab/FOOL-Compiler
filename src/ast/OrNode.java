package ast;

import lib.FOOLlib;
import visitors.NodeVisitor;

public class OrNode implements Node {

	private Node left;
	private Node right;

	public OrNode( Node left, Node right ) {
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
	
	
	
	
	
	
	
	
	
	






	/*
	 * crea due etichette fresh. Per vedere se i due valori di codeGeneration sono
	 * uguali (booleani dove 0 = false e 1 = true) allora basta SOMMARE i due
	 * valori. Infatti nell'or se la somma fa 0 significa che sono entrambi 0 e
	 * quindi torner� false. Dopodich� confronta questa somma con 0 che viene
	 * pushato e se sono uguali allora l'or � false e salta a l1 dove viene pushato
	 * 0 che � il valore di ritorno false, se sono diversi allora � true, pusha 1
	 * che � il valore di ritorno true e salta a l2.
	 * 
	 * La logica � invertita rispetto all'AND.
	 */
	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return left.codeGeneration() + right.codeGeneration() + "add\n" + "push 0\n" + "beq " + l1 + "\n" + "push 1\n"
				+ "b " + l2 + "\n" + l1 + ": \n" + "push 0\n" + l2 + ": \n";
	}

}
