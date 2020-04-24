package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents an object instantiation
 * 
 * @author Paolo Baldini
 */
public class NewNode implements Node {

	private String ID;			// instantiated class name
	private STEntry entry;		// class' symbol-table-entry
	private List<Node> fields;	// passed fields' values

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
	
	public void addField( Node field ) {
		fields.add( field );
	}
	
	public List<Node> getFields( ) {
		return fields;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
