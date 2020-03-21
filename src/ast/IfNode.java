package ast;

import lib.*;
import visitors.Visitor;

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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	



	public Node typeCheck() throws TypeException {
		if (!(FOOLlib.isSubtype(condition.typeCheck(), new BoolTypeNode())))
			throw new TypeException("Non boolean condition in if");
		Node t = thenBranch.typeCheck();
		Node e = elseBranch.typeCheck();
		if (FOOLlib.isSubtype(t, e))
			return e;
		if (FOOLlib.isSubtype(e, t))
			return t;

		Node n = FOOLlib.lowestCommonAncestor(t, e);
		if (n == null)
			throw new TypeException("Incompatible types in then-else branches");

		return n;
	}

	public String codeGeneration() {
		String l1 = FOOLlib.freshLabel();
		String l2 = FOOLlib.freshLabel();
		return condition.codeGeneration() + "push 1\n" + "beq " + l1 + "\n" + elseBranch.codeGeneration() + "b " + l2 + "\n" + l1
				+ ": \n" + thenBranch.codeGeneration() + l2 + ": \n";
	}
}