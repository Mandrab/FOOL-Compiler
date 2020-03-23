package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class NewNode implements Node {

	private String ID;
	private STentry entry;
	private List<Node> fields;

	public NewNode( String name, STentry entry ) {
		this.ID = name;
		this.entry = entry;
		this.fields = new ArrayList<Node>( );
	}
	
	public String getID( ) {
		return ID;
	}

	public STentry getEntry( ) {
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
