package ast;

import java.util.ArrayList;
import java.util.List;

import lib.TypeException;

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
	
	public void setField( Node field ) {
		this.allFields.add( field );
	}
	
	public void setAllFields(List<Node> allFields) {
		this.allFields = allFields;
	}

	public List<Node> getAllFields( ) {
		return allFields;
	}
	
	public void setMethod( Node method ) {
		this.allMethods.add( method );
	}
	
	public void setAllMethods(List<Node> allMethods) {
		this.allMethods = allMethods;
	}
	
	public List<Node> getAllMethods( ) {
		return allMethods;
	}

	@Override
	public String toPrint( String indent ) {
		// TODO Auto-generated method stub
		return null;
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
