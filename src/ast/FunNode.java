package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents a funtion
 * 
 * @author Paolo Baldini
 */
public class FunNode implements Node, DecNode {

	private String ID;					// function ID
	private Node returnType;			// function's return-type
	private List<Node> parameters;		// function's required parameters' types
	private List<Node> declarations;	// variables declarations in function's body (let-in block)
	private Node exp;					// function's main expression

	public FunNode( String id, Node type ) {
		this.ID = id;
		this.returnType = type;
		this.parameters = new ArrayList<Node>( );
		this.declarations = new ArrayList<Node>( );
	}
	
	public String getID( ) {
		return ID;
	}
	
	@Override
	public Node getSymType( ) {
		return returnType;
	}
	
	public void addParameter( Node parameter ) {
		parameters.add( parameter );
	}
	
	public List<Node> getParameters( ) {
		return parameters;
	}

	public void addDeclaration( Node declaration ) {
		declarations.add( declaration );
	}
	
	public List<Node> getDeclarations( ) {
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