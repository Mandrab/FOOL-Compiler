package ast;

import lib.*;
import visitors.Visitor;

public class VarNode implements Node, DecNode {

	private String ID;
	private Node type;
	private Node exp;

	public VarNode( String id, Node type, Node expression ) {
		this.ID = id;
		this.type = type;
		this.exp = expression;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public Node getSymType( ) {
		return type;
	}
	
	public Node getExp( ) {
		return exp;
	}

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}

	
	
	
	
	
	
	
	
	


	public Node typeCheck() throws TypeException {
		if (!FOOLlib.isSubtype(exp.typeCheck(), type))
			throw new TypeException("Incompatible value for variable " + ID);
		return null;
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}

	

	

}