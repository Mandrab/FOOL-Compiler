package ast;

import visitors.NodeVisitor;

public class ProgNode implements Node {

	private Node exp;

	public ProgNode( Node expression ) {
		exp = expression;
	}
	
	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	



	


	public String codeGeneration() {
		return exp.codeGeneration() + "halt\n";
	}
}