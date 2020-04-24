package ast;

import visitors.NodeVisitor;

/**
 * Represents a class that's visitable by a visitor
 * 
 * @author Paolo Baldini
 */
public interface Visitable {

	public <T> T accept( NodeVisitor<T> visitor );
}
