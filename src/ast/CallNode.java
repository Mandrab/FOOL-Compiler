package ast;

import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents function call (or method one if called by another method of the same class)
 * 
 * @author Paolo Baldini
 */
public class CallNode implements Node {

	private String ID;				// function ID
	private int nestingLevel;		// nl of the call
	private STEntry definition;		// function symbol-table-entry
	private List<Node> parameters;	// passed parameters

	public CallNode( String id, STEntry stEntry, List<Node> pars, int nl ) {
		ID = id;
		definition = stEntry;
		parameters = pars;
		nestingLevel = nl;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public int getNestingLevel( ) {
		return nestingLevel;
	}
	
	public STEntry getEntry( ) {
		return definition;
	}
	
	public List<Node> getParameters( ) {
		return parameters;
	}
	
	public Node getType( ) {
		return definition.getRetType( );
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}