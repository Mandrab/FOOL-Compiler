package ast;

import visitors.NodeVisitor;

/**
 * Represents 'null' type in AST
 * 
 * @author Paolo Baldini
 */
public class EmptyTypeNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
