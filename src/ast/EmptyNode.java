package ast;

import visitors.NodeVisitor;

public class EmptyNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
