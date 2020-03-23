package ast;

import visitors.NodeVisitor;

/*Tipo null. Utilizzato da EmptyNode come tipo di ritorno.*/
public class EmptyTypeNode implements Node {

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}

}
