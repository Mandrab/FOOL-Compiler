package ast;
import lib.*;

public class VarNode implements Node, DecNode {

	private String id;
	private Node type;
	private Node exp;

	public VarNode (String i, Node t, Node v) {
		id=i;
		type=t;
		exp=v;
	}

	public String toPrint(String s) {
		return s+"Var:" + id +"\n"
				+type.toPrint(s+"  ")  
				+exp.toPrint(s+"  ");
	}

	public Node typeCheck() throws TypeException {
		if (! FOOLlib.isSubtype(exp.typeCheck(),type)) 
			throw new TypeException("Incompatible value for variable "+id);
		return null;
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}

	public Node getSymType() {
		return type;
	}

}  