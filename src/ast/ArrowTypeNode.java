package ast;

import java.util.List;

import visitors.Visitor;

public class ArrowTypeNode implements Node {

	private List<Node> parameters;
	private Node returnType;

	public ArrowTypeNode( List<Node> parameters, Node returnType ) {
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public Node getRetType() {
		return returnType;
	}

	public List<Node> getParameters() {
		return parameters;
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit( this );
	}
	
	
	
	
	
	



	// non utilizzato
	public Node typeCheck() {
		return null;
	}

	// non utilizzato
	public String codeGeneration() {
		return "";
	}

}