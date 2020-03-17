package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lib.TypeException;

public class NewNode implements Node {

	private STentry entry;
	private List<Node> fields;

	public NewNode( STentry entry ) {
		this.entry = entry;
		fields = new ArrayList<Node>( );
	}
	
	public void setField( Node field ) {
		fields.add( field );
	}

	@Override
	public String toPrint( String indent ) {
		return indent + "Class: " + "\n" +
				fields.stream( ).map( s -> s.toPrint( indent + "\t" ) ).collect( Collectors.joining( ) );
	}

	@Override
	public Node typeCheck() throws TypeException {
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
