package ast;

import lib.*;
import visitors.NodeVisitor;

public class IfNode implements Node {

	private Node condition;
	private Node thenBranch;
	private Node elseBranch;

	public IfNode( Node condition, Node thenBranch, Node elseBranch ) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
	
	public Node getCondition( ) {
		return condition;
	}

	public Node getThenBranch( ) {
		return thenBranch;
	}

	public Node getElseBranch( ) {
		return elseBranch;
	}

	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	





	

	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return condition.codeGeneration() + "push 1\n" + "beq " + l1 + "\n" + elseBranch.codeGeneration() + "b " + l2 + "\n" + l1
				+ ": \n" + thenBranch.codeGeneration() + l2 + ": \n";
	}
}