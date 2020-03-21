package ast;

import java.util.ArrayList;
import java.util.List;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	
		
	
	
	

	
	
	
	


	@Override
	public Node typeCheck() throws TypeException {
		for (Node m : methods)
			try {
				m.typeCheck();
			} catch (TypeException e) {
				System.out.println("Type checking error in a method: " + e.text);
			}
		
		if(superEntry != null) {
			ClassTypeNode classTypeNSuper = (ClassTypeNode) superEntry.getRetType();
			int minOffset = ((FieldNode)(classTypeNSuper.getFields().get(0))).getOffset();
			int maxOffset = ((FieldNode)(classTypeNSuper.getFields().get(classTypeNSuper.getFields().size()-1))).getOffset();
			int count = 0;
			
			for(Node f: fields) {
				if( ((FieldNode) f).getOffset() >= minOffset && ((FieldNode) f).getOffset() <= maxOffset) {
					if (!FOOLlib.isSubtype(((FieldNode)f).getSymType(), ((FieldNode)(classTypeNSuper.getFields().get(count))).getSymType()))
						throw new TypeException(" [ClassNode] Type check found a problem: \n Wrong overriding in field: " + ((FieldNode)f).getID());
				}
				count++;
			}
			
			minOffset = ((MethodNode)(classTypeNSuper.getMethods().get(0))).getOffset();
			maxOffset = ((MethodNode)(classTypeNSuper.getMethods().get(classTypeNSuper.getMethods().size()-1))).getOffset();
			count = 0;
			
			for(Node m: methods) {
				if( ((MethodNode) m).getOffset() >= minOffset && ((MethodNode) m).getOffset() <= maxOffset) {
					if (!FOOLlib.isSubtype(((MethodNode)m).getSymType(), ((MethodNode)(classTypeNSuper.getFields().get(count))).getSymType()))
						throw new TypeException(" [ClassNode] Type check found a problem: \n Wrong overriding in method: " + ((MethodNode)m).getID());
				}
				count++;
			}
		}
		
		return null; //TODO giusto?!?!?
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
