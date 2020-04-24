package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents the definition of a class
 * 
 * @author Paolo Baldini
 */
public class ClassNode implements DecNode, Node {

	private String ID;			// class ID
	private STEntry superEntry;	// optional super-type
	private Node symType;  		// class-type
	private List<Node> fields;	// list of required fields
	private List<Node> methods;	// list of methods
	
	
	public ClassNode( String name, Node type ) {
		this.symType = type;
		this.superEntry = null;
		this.ID = name;
		this.fields = new ArrayList<>( );
		this.methods = new ArrayList<>( );
	}
	
	public String getID( ) {
		return ID;
	}
	
	public void setSuper( STEntry superEntry ) {
		this.superEntry = superEntry;
	}
	
	public STEntry getSuper( ) {
		return superEntry;
	}
	
	public Node getSymType( ) {
		return symType;
	}
	
	public void addField( Node field ) {
		fields.add( field );
	}
	
	public void addFields( List<Node> fields ) {
		this.fields.addAll( fields );
	}
	
	public List<Node> getFields( ) {
		return fields;
	}
	
	public void addMethod( Node method ) {
		methods.add( method );
	}
	
	public void addMethods( List<Node> methods ) {
		this.methods.addAll( methods );
	}
	
	public List<Node> getMethods( ) {
		return methods;
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
