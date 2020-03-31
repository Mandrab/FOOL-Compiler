package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class NewNode implements Node {

	private String ID;
	private STEntry entry;
	private List<Node> fields;

	public NewNode( String name, STEntry entry ) {
		this.ID = name;
		this.entry = entry;
		this.fields = new ArrayList<Node>( );
	}
	
	public String getID( ) {
		return ID;
	}

	public STEntry getEntry( ) {
		return entry;
	}
	
	public void addField(Node field) {
		fields.add(field);
	}
	
	public List<Node> getFields( ) {
		return fields;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
