package ast;

import java.util.List;

import visitors.NodeVisitor;

/**
 * Represents a functional-type in the AST
 * 
 * @author Paolo Baldini
 */
public class ArrowTypeNode implements Node {

	private List<Node> parameters;
	private Node returnType;

	public ArrowTypeNode( List<Node> parameters, Node returnType ) {
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public Node getRetType( ) {
		return returnType;
	}

	public List<Node> getParameters( ) {
		return parameters;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}