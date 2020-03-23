package ast;

import visitors.NodeVisitor;

public class IdNode implements Node {

	private String ID;
	private STentry entry;
	private int nestingLevel;

	public IdNode( String id, STentry stEntry, int nestingLevel ) {
		this.ID = id;
		this.nestingLevel = nestingLevel;
		this.entry = stEntry;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public STentry getEntry( ) {
		return entry;
	}
	
	public int getNestingLevel() {
		return nestingLevel;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}