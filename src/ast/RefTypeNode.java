package ast;

import lib.TypeException;

public class RefTypeNode implements Node {

	private String ID;
	
	public RefTypeNode( String id ) {
		ID = id;
	}
	
	public String getID( ) {
		return ID;
	}
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node typeCheck() throws TypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
