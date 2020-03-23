package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class FunNode implements Node, DecNode {

	private String ID;
	private Node type;
	private List<Node> parameters; // campo "parameters" che � lista di Node
	private List<Node> declarations;
	private Node exp;

	public FunNode( String id, Node type ) {
		this.ID = id;
		this.type = type;
		this.parameters = new ArrayList<Node>( );
		this.declarations = new ArrayList<Node>();
	}
	
	public String getID( ) {
		return ID;
	}
	
	@Override
	public Node getSymType( ) {
		return type;
	}
	
	public void addParameter( Node parameter ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		parameters.add( parameter );
	}
	
	public List<Node> getParameters( ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		return parameters;
	}

	public void addDeclaration( Node declaration ) {
		declarations.add( declaration );
	}
	
	public List<Node> getDeclarations( ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		return declarations;
	}

	public void setExpession( Node expression ) {
		exp = expression;
	}
	
	public Node getExpession( ) {
		return exp;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}