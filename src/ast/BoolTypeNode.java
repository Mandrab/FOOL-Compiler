package ast;

import visitors.NodeVisitor;

/**
 * Represents boolean-type in the AST
 * 
 * @author Paolo Baldini
 */
public class BoolTypeNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}