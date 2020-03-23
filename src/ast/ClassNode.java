package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class ClassNode implements DecNode, Node {

	private String ID;
	private STentry superEntry;
	private Node symType;  //ClassTypeNode
	private List<Node> fields;	//fieldNode
	private List<Node> methods;	//methodNode
	
	
	public ClassNode( Node type, String name ) {
		this.symType = type;
		this.superEntry = null;
		this.ID = name;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
	}
	
	public String getID( ) {
		return ID;
	}
	
	public void setSuper( STentry superEntry ) {
		this.superEntry = superEntry;
	}
	
	public STentry getSuper( ) {
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
