package ast;

import java.util.ArrayList;
import java.util.List;

import visitors.NodeVisitor;

public class ClassCallNode implements Node {
	
	private String ID;
	private int nestingLevel;
	private STentry entry;	//Tipo dell'obj su cui richiamo il metodo
	private STentry methodEntry; //Mio metodo
	private List<Node> parameters; //lista di parametri passati
	
	
	public ClassCallNode( String ID, STentry entry, STentry methodEntry, int nestingLevel ) {
		this.ID = ID;
		this.entry = entry;
		this.methodEntry = methodEntry;
		this.nestingLevel = nestingLevel;
		
		parameters = new ArrayList<Node>( );
	}
	
	public String getID( ) {
		return ID;
	}

	public int getNestingLevel( ) {
		return nestingLevel;
	}

	public STentry getEntry( ) {
		return entry;
	}

	public STentry getMethodEntry( ) {
		return methodEntry;
	}
	
	public void addParameter( Node parameter ) {
		parameters.add( parameter );
	}

	public List<Node> getParameters( ) {
		return parameters;
	}

	public Node getRetType() {
		return methodEntry.getRetType( );
	}
	
	@Override
	public <T> T accept( NodeVisitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	


	

	@Override
	public String codeGeneration() {

		String parCode = "";
		for (int i = parameters.size() - 1; i >= 0; i--)
			parCode += parameters.get(i).codeGeneration();
		String getAR = "";
		for (int i = 0; i < nestingLevel - entry.getNestingLevel(); i++)
			getAR += "lw\n";

		return "lfp\n" +// push Control Link (pointer to frame of function id caller)
				parCode +// generate code for parameter expressions in reversed order
				"lfp\n" +
				getAR + // Find the correct AR address.
				"push " + entry.getOffset() + "\n" + // push indirizzo ad AR dichiarazione funzione, recuperato a offset ID 
				"add\n"+ //Cos� ho l'Obj Pointer dell'obj nell'AR
				"lw\n" +//Carico sullo stack
				"stm\n" + "ltm\n" + "ltm\n" + // duplico il val sullo stack
				"lw\n" + // stack <- indirizzo dt --- paul (?)
				"push " + methodEntry.getOffset() + "\n"+
				"add\n" +
				"lw\n" + //Carico sullo stack
				"js\n"; //Salto all'indirizzo
	}

}
