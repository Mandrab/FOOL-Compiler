package ast;

import visitors.NodeVisitor;

/**
 * Represents a print instruction (print the top value on the stack to stdout)
 * 
 * @author Paolo Baldini
 */
public class PrintNode implements Node {

	private Node exp;

	public PrintNode( Node expression ) {
		exp = expression;
	}

	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}