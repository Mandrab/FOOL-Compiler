package ast;

import java.util.ArrayList;
import java.util.List;

import lib.TypeException;
import visitors.Visitor;

/***
 * Contiene tutti i campi/metodi (anche quelli ereditati)
 * Usato per TypeCheck
 * @author Sophia Fantoni
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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	

	@Override
	public Node typeCheck() throws TypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
