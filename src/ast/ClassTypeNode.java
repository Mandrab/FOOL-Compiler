package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/***
 * Contiene tutti i campi/metodi (anche quelli ereditati)
 * Usato per TypeCheck
 *
 */
public class ClassTypeNode implements Node {
	
	private List<Node> allFields;
	private List<Node> allMethods;
	
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
