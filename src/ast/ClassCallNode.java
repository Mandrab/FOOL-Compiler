package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents a method call
 * 
 * @author Paolo Baldini
 */
public class ClassCallNode implements Node {

	private String ID;				// method ID
	private int nestingLevel;		// nl of the call
	private STEntry entry;			// obj's class entry
	private STEntry methodEntry;	// called method
	private List<Node> parameters;	// passed parameters


	public ClassCallNode( String ID, STEntry entry, STEntry methodEntry, int nestingLevel ) {
		this.ID = ID;
		this.entry = entry;
		this.methodEntry = methodEntry;
		this.nestingLevel = nestingLevel;

		parameters = new ArrayList<Node>( );
	}

	public String getID( ) {
		return ID;
	}

	public int getNestingLevel( ) {
		return nestingLevel;
	}

	public STEntry getEntry( ) {
		return entry;
	}

	public STEntry getMethodEntry( ) {
		return methodEntry;
	}

	public void addParameter( Node parameter ) {
		parameters.add( parameter );
	}

	public List<Node> getParameters( ) {
		return parameters;
	}

	public Node getRetType( ) {
		return methodEntry.getRetType( );
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
