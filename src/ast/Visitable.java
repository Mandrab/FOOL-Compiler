package ast;

import visitors.NodeVisitor;

public interface Visitable {

	public <T> T accept( NodeVisitor<T> visitor );
}
