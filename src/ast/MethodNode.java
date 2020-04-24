package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents a method
 * 
 * @author Paolo Baldini
 */
public class MethodNode implements DecNode, Node {

	private String ID;					// method ID
	private Node returnType;			// method's return type
	private List<Node> parameters;		// method's required parameters' types
	private List<Node> declarations;	// variables declarations in method's body (let-in block)
	private Node exp;					// mathod's main expression
	private int offset;					// method's offset in class definition
	private String generatedLabel;		// assembly sub-routine's label

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
