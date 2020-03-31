package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class ClassCallNode implements Node {
	
	private String ID;
	private int nestingLevel;
	private STEntry entry;	//Tipo dell'obj su cui richiamo il metodo
	private STEntry methodEntry; //Mio metodo
	private List<Node> parameters; //lista di parametri passati
	
	
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

	public Node getRetType() {
		return methodEntry.getRetType( );
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
