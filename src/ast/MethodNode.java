package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class MethodNode implements DecNode, Node {

	private String ID;
	private Node returnType;
	private List<Node> parameters; // campo "parameters" che e' lista di Node
	private List<Node> declarations = new ArrayList<Node>(); //let in
	private Node exp;
	private int offset;
	private String generatedLabel;

	public MethodNode( String id, Node returnType ) {
		this.ID = id;
		this.returnType = returnType;
		this.parameters = new ArrayList<Node>( );
		this.declarations = new ArrayList<Node>( );
		this.offset = -1;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public Node getSymType( ) {
		return returnType;
	}
	
	public void addParameter( Node parameter ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		parameters.add( parameter );
	}
	
	public List<Node> getParameters( ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		return parameters;
	}
	
	public void addDeclaration( Node declaration ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
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
	
	public void setOffset( int offset ) {
		this.offset = offset;
	}
	
	public int getOffset( ) {
		return offset;
	}
	
	public void setLabel( String label ) {
		generatedLabel = label;
	}

	public String getLabel( ) {
		return generatedLabel;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
