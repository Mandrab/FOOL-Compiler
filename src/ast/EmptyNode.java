package ast;

import visitors.NodeVisitor;

/**
 * Represents 'null' value
 * 
 * @author Paolo Baldini
 */
public class EmptyNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
