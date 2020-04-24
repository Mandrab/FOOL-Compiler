package ast;

import visitors.NodeVisitor;

/**
 * Represents an identifier in the AST
 * 
 * @author Paolo Baldini
 */
public class IdNode implements Node {

	private String ID;			// identifier's name
	private STEntry entry;		// symbol-table entry of the identifier's declaration
	private int nestingLevel;	// nl of use

	public IdNode( String id, STEntry stEntry, int nestingLevel ) {
		this.ID = id;
		this.nestingLevel = nestingLevel;
		this.entry = stEntry;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public STEntry getEntry( ) {
		return entry;
	}
	
	public int getNestingLevel( ) {
		return nestingLevel;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}