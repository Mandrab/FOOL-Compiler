package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents the type of a class. I.e., contains all class' fields and methods definitions
 * (included the ones of the optional super-type)
 * 
 * @author Paolo Baldini
 */
public class ClassTypeNode implements Node {

	private List<Node> allFields;	// fields types
	private List<Node> allMethods;	// methods types

	public ClassTypeNode( ) {
		allFields = new ArrayList<>( );
		allMethods = new ArrayList<>( );
	}

	public void addField( Node field ) {
		allFields.add( field );
	}

	public void addFields( List<Node> fields ) {
		allFields.addAll( fields );
	}

	public List<Node> getFields( ) {
		return allFields;
	}

	public void addMethod( Node method ) {
		allMethods.add( method );
	}

	public void addMethods( List<Node> methods ) {
		allMethods.addAll( methods );
	}

	public List<Node> getMethods( ) {
		return allMethods;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
