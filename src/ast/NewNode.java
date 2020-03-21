package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

public class NewNode implements Node {

	private String ID;
	private STentry entry;
	private List<Node> fields;

	public NewNode( STentry entry, String name ) {
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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}

	

	
	
	
	
	


	
	

	@Override
	public Node typeCheck() throws TypeException {
		if (!(entry.getRetType() instanceof ClassTypeNode))
			throw new TypeException("Invocation of a non-class " + this.ID);

		RefTypeNode refTypeNode = new RefTypeNode(this.ID);

		ClassTypeNode ctn = (ClassTypeNode) entry.getRetType();
		List<Node> requiredFields = ctn.getFields();

		if (!(requiredFields.size() == fields.size()))
			throw new TypeException("[NewNode] Wrong number of parameters in the invocation of " + this.ID + " p.size"
					+ ctn.getFields().size() + " fieldSize:" + fields.size());
		int count = 0;

		Node f = null;
		FieldNode ef = null;

		for (Node field : fields) {
			f = field;
			ef = (FieldNode) requiredFields.get(count);

			if (f instanceof IdNode)
				f = ((IdNode) f).getEntry().getRetType();
			else if (f instanceof DecNode)
				f = ((DecNode) f).getSymType();
			else if (f instanceof CallNode)
				f = ((CallNode) f).getRetType();
			else if (f instanceof ClassCallNode)
				f = ((ClassCallNode) f).getRetType();
			else
				f = f.typeCheck();

			if (f instanceof ArrowTypeNode)
				f = ((ArrowTypeNode) f).getRetType();

			if (!(FOOLlib.isSubtype(f, ef.getSymType()))) {
				throw new TypeException("Wrong type of fields " + ef.getID());
			}
			count++;
		}
		return refTypeNode;
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
