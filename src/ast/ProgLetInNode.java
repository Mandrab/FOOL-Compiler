package ast;

import visitors.NodeVisitor;

import java.util.List;

/**
 * Represents a program with declarations (let-in block)
 * 
 * @author Paolo Baldini
 */
public class ProgLetInNode implements Node {

	private List<Node> declarations;	// program's declarations (class, function, variables)
	private Node exp;					// program main expression

	public ProgLetInNode( List<Node> declarations, Node expression ) {
		this.declarations = declarations;
		this.exp = expression;
	}

	public List<Node> getDeclarations( ) {
		return declarations;
	}

	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}