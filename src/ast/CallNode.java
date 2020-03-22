package ast;

import java.util.List;

import visitors.NodeVisitor;

public class CallNode implements Node {

	private String ID;
	private int nestingLevel;
	private STentry definition;
	private List<Node> parameters;

	public CallNode( String id, STentry stEntry, List<Node> pars, int nl ) {
		ID = id;
		definition = stEntry;
		parameters = pars;
		nestingLevel = nl;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public int getNestingLevel( ) {
		return nestingLevel;
	}
	
	public STentry getEntry() {
		return definition;
	}
	
	public List<Node> getParameters( ) {
		return parameters;
	}
	
	public Node getType( ) {
		return definition.getRetType( );
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	
	


	
	


	public String codeGeneration() {
		String parCode = "";
		for (int i = parameters.size() - 1; i >= 0; i--)
			parCode += parameters.get(i).codeGeneration();
		String getAR = "";
		for (int i = 0; i < nestingLevel - definition.getNestingLevel(); i++)
			getAR += "lw\n";
		
		if(definition.isMethod()) {
			return  "lfp\n"+ // push Control Link (pointer to frame of function id caller)
			         parCode+ // generate code for parameter expressions in reversed order
			         "lfp\n"+
		             getAR+ // push Access Link (pointer to frame of function id declaration, reached as for variable id)
			         "stm\n"+"ltm\n"+"ltm\n"+ // duplicate top of the stack
			         "lw\n" + //vado nella DT
			         "push "+ definition.getOffset() + "\n"+ //pusho indirizzo della funzione recuperato a offset ID
					 "add\n"+
		             "lw\n"+ // push function address (value at: pointer to frame of function id declaration + its offset)
			         "js\n";// jump to popped address (putting in $ra address of subsequent instruction)
		} else {
			return "lfp\n" +// push Control Link (pointer to frame of function id caller)
					parCode +// generate code for parameter expressions in reversed order
					"lfp\n" +
					getAR + // Find the correct AR address.
					"push " + definition.getOffset() + "\n" + // push indirizzo ad AR dichiarazione funzione, recuperato a offset ID
					"add\n" + 
					"stm\n" + // duplicate top of the stack.
					"ltm\n" +
					"lw\n" + 
					"ltm\n" + // ripusho l'indirizzo ottenuto precedentemente, per poi calcolarmi offset ID - 1
					"push 1\n" + // push 1, 
					"sub\n" + // sottraggo a offset ID - 1, per recuperare l'indirizzo funzione.
					"lw\n" + // push function address.
					"js\n";
		}
	}
}