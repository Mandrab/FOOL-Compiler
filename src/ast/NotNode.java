package ast;

import visitors.NodeVisitor;

public class NotNode implements Node {

	private Node exp;

	public NotNode( Node exp ) {
		this.exp = exp;
	}
	
	public Node getExpression( ) {
		return exp;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	
	





	

	/*
	 * Per fare il not di un booleano pusha 1, poi il valore del booleano (0 false,
	 * 1 true) e poi sottrae. Cos� se era true (1) con la sottrazione fa a 0
	 * (false). Viceversa se era false (0) con la sottrazione va a 1 (true).
	 */
	public String codeGeneration() {
		return "push 1\n" + exp.codeGeneration() + "sub\n";
	}

}
