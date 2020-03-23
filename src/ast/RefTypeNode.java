package ast;

import visitors.NodeVisitor;

public class RefTypeNode implements Node {

	private String ID;
	
	public RefTypeNode( String ID ) {
		this.ID = ID;
	}
	
	public String getID( ) {
		return ID;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
