package ast;

import java.util.ArrayList;
import java.util.List;

import lib.TypeException;

public class ClassCallNode implements Node {
	
	private int nestingLevel;
	private STentry entry;
	private STentry methodEntry;
	private List<Node> parameters;
	
	public ClassCallNode( STentry entry, STentry methodEntry, int nestingLevel ) {
		this.entry = entry;
		this.methodEntry = methodEntry;
		this.nestingLevel = nestingLevel;
		parameters = new ArrayList<Node>( );
	}

	public void addPar( Node parameter ) {
		parameters.add( parameter );
	}
	
	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node typeCheck() throws TypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
