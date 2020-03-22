package ast;

import java.util.ArrayList;
import java.util.List;

import lib.FOOLlib;
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
	
	
	
	
	
	
	
		
	
	
	

	
	
	
	




	@Override
	public String codeGeneration() {
		ArrayList<String> myDispatchTable = new ArrayList<>();
		FOOLlib.dispatchTable.add(myDispatchTable);
		String lab = "";
		int labOffset;
		int sizeMethod = 0;
		if ( superEntry != null ) {
			sizeMethod = ((ClassTypeNode)(this.superEntry.getRetType())).getMethods().size();
			List<String> superLabel = FOOLlib.dispatchTable.get(-2-superEntry.getOffset());
			
			for(String s : superLabel) {
				myDispatchTable.add(s);
			};
		}
		
		String labellist = "";
		
		for(Node m : this.methods) {
			((MethodNode)m).codeGeneration();
			lab = ((MethodNode)m).getLabel();
			labOffset = ((MethodNode)m).getOffset();
			
			if(labOffset < sizeMethod) { //check override
				myDispatchTable.remove(labOffset);
				myDispatchTable.add(labOffset, lab);
			} else {
				myDispatchTable.add(lab);	
			}
		}
		
		//Per ogni etichetta
		for (String s:myDispatchTable) {
			labellist += ("push " + s + "\n" ); //push label
			labellist += ("lhp\n"); //push hp
			labellist += ("sw\n");
			labellist += ("push 1\n" + "lhp\n" + "add\n" + "shp\n"); //hp++
		}

		return "lhp\n" + labellist;
	}

}
