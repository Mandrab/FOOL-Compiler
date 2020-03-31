package ast;

import java.util.List;

import visitors.NodeVisitor;

public class CallNode implements Node {

	private String ID;
	private int nestingLevel;
	private STEntry definition;
	private List<Node> parameters;

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
	
	public STEntry getEntry() {
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