package ast;

import visitors.NodeVisitor;

/**
 * A class used to store informations in symbol table
 * 
 * @author Paolo Baldini
 */
public class STEntry implements Visitable {

	private int nestingLevel;	// nesting level of Activation Record of declaration
	private int offset;			// offset of declaration in AR
	private Node type;			// type of declaration
	private boolean isMethod;	// true if is the declaration of a method

	public STEntry( int nestingLevel, Node type, int offset ) {
		this.nestingLevel = nestingLevel;
		this.offset = offset;
		this.type = type;
		this.isMethod = false;
	}

	public STEntry( int nestingLevel, Node type, int offset, boolean isMethod ) {
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

	public boolean isMethod( ) {
		return isMethod;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}