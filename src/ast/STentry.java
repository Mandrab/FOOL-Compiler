package ast;

import visitors.NodeVisitor;

public class STentry implements Visitable {

	private int nestingLevel;
	private int offset;
	private Node type;
	private boolean isMethod;

	public STentry( int nestingLevel, Node type, int offset ) {
		this.nestingLevel = nestingLevel;
		this.offset = offset;
		this.type = type;
		this.isMethod = false;
	}

	public STentry( int nestingLevel, Node type, int offset, boolean isMethod ) {
		this.nestingLevel = nestingLevel;
		this.offset = offset;
		this.type = type;
		this.isMethod = isMethod;
	}
	
	public int getNestingLevel( ) {
		return nestingLevel;
	}
	
	public int getOffset( ) {
		return offset;
	}
	
	public Node getRetType(	) {
		return type;
	}
	
	public boolean isMethod( )  {
		return isMethod;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
}