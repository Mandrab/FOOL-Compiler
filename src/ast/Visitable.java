package ast;

import visitors.Visitor;

public interface Visitable {

	public <T> T accept( Visitor<T> visitor );
}
