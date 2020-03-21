package ast;

import lib.TypeException;
import visitors.Visitor;

public class RefTypeNode implements Node {

	private String ID;
	
	public RefTypeNode( String ID ) {
		this.ID = ID;
	}
	
	public String getID( ) {
		return ID;
	}
	
	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	
	
	


	@Override
	public Node typeCheck() throws TypeException {
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
