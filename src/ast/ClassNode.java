package ast;

import java.util.List;

import lib.TypeException;

/***
 * Definisce la mia struttura
 * @author Sophia Fantoni
 *
 */
public class ClassNode implements DecNode, Node {

	private STentry superEntry;
	private Node symType;
	private List<Node> fields;	//fieldNode
	private List<Node> methods;	//methodNode
	
	public ClassNode( Node type ) {
		this.symType = type;
		this.superEntry = null;
	}
	
	public void setSuper( STentry superEntry ) {
		this.superEntry = superEntry;
	}
	
	public STentry getSuper() {
		return this.superEntry;
	}
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setField( Node field ) {
		this.fields.add( field );
	}
	
	public void setAllFields(List<Node> allFields) {
		this.fields = allFields;
	}

	public List<Node> getAllFields( ) {
		return fields;
	}
	
	public void setMethod( Node method ) {
		this.methods.add( method );
	}
	
	public void setAllMethods(List<Node> allMethods) {
		this.methods = allMethods;
	}
	
	public List<Node> getAllMethods( ) {
		return methods;
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

	@Override
	public Node getSymType() {
		return symType;
	}

}
