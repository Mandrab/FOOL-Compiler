package ast;

public class FieldNode implements DecNode, Node {

	private String id;
	private Node type;
	private int offset;

	public FieldNode(String i, Node t) {
		id = i;
		type = t;
		this.offset = 0;
	}

	public String toPrint(String s) {
		return s + "Par:" + id + "\n" + type.toPrint(s + "  ");
	}

	// non utilizzato
	public Node typeCheck() {
		return null;
	}

	// non utilizzato
	public String codeGeneration() {
		return "";
	}

	@Override
	public Node getSymType() {
		return type;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public void setOffset(int o) {
		this.offset = o;
	}

}
