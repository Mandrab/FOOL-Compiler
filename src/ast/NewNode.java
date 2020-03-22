package ast;

import java.util.ArrayList;
import java.util.List;

import lib.FOOLlib;
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

	

	
	
	
	
	


	
	



	@Override
	public String codeGeneration() {

		String valList = "";

		for (Node f : this.fields) {
			valList += f.codeGeneration(); // fa push dei valori sullo stack
		}

		for (int i = 0; i < fields.size(); i++) {
			valList += ("lhp " + "\n"); // push hp
			valList += ("sw\n");
			valList += ("push 1\n" + "lhp\n" + "add\n" + "shp\n"); // hp++
		}

		valList += ("push " + (entry.getOffset() + FOOLlib.MEMSIZE) + "\n" + // push DP
				"lw\n" + "lhp\n" + "sw\n" + "lhp\n");

		return valList + ("push 1\n" + "lhp\n" + "add\n" + "shp\n");
	}

}
