package ast;

import lib.FOOLlib;
import lib.TypeException;

public class MinusNode implements Node {

	private Node left;
	private Node right;

	public MinusNode (Node l, Node r) {
		left=l;
		right=r;
	}
	@Override
	public String toPrint(String s) {
		return s+"Minus\n" + left.toPrint(s+"  ")  
		+ right.toPrint(s+"  ") ; 
	}
	@Override
	public Node typeCheck() throws TypeException {
		if ( ! ( FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode()) &&
				FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode()) ) ) 
			throw new TypeException("Non integers in subtraction");
		return new IntTypeNode();
	}
	@Override
	public String codeGeneration() {
		return left.codeGeneration()+right.codeGeneration()+"sub\n";
	}

}
