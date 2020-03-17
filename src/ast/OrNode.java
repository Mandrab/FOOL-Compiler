package ast;

import lib.FOOLlib;
import lib.TypeException;

public class OrNode implements Node {

	private Node left;
	private Node right;

	public OrNode (Node l, Node r) {
		left=l;
		right=r;
	}

	public String toPrint(String s) {
		return s+"Or\n" + left.toPrint(s+"  ")  
		+ right.toPrint(s+"  ") ; 
	}
	/*Da riguardare. E' possibile l'and solo tra booleani*/
	public Node typeCheck() throws TypeException {
		Node l= left.typeCheck();  
		Node r= right.typeCheck();  
		if (!(l instanceof BoolTypeNode && r instanceof BoolTypeNode))
			throw new TypeException("Incompatible types in or");
		return new BoolTypeNode();
	}
	
	/* crea due etichette fresh. Per vedere se i due valori di codeGeneration sono uguali (booleani dove
	 * 0 = false e 1 = true) allora basta SOMMARE i due valori. Infatti nell'or se la somma fa 0
	 * significa che sono entrambi 0 e quindi tornerà false. Dopodichè confronta questa somma con 0 
	 * che viene pushato e se sono uguali allora l'or è false e salta a l1 dove viene pushato 0 che è il valore di ritorno
	 * false, se sono diversi allora è true, pusha 1 che è il valore di ritorno true e salta a l2.
	 * 
	 * La logica è invertita rispetto all'AND.
	 */
	public String codeGeneration() {
		String l1= FOOLlib.freshLabel();
	    String l2= FOOLlib.freshLabel();
		  return left.codeGeneration()+
				 right.codeGeneration()+
				 "add\n"+
				 "push 0\n"+
				 "beq "+l1+"\n"+
				 "push 1\n"+
				 "b "+l2+"\n"+
				 l1+": \n"+
				 "push 0\n"+
				 l2+": \n";	   
	}

}
