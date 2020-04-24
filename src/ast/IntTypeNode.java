package ast;

import visitors.NodeVisitor;

/**
 * Represents an integer-type in the AST
 * 
 * @author Paolo Baldini
 */
public class IntTypeNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}