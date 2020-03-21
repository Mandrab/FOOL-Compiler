package ast;

import java.util.ArrayList;
import java.util.List;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

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
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	


	@Override
	public Node typeCheck() throws TypeException {
		if (!(methodEntry.getRetType() instanceof ArrowTypeNode)) {
			System.out.println(methodEntry.getRetType().getClass());
			throw new TypeException("Invocation of a non-method " + this.ID);
		}

		ArrowTypeNode arrowNode = (ArrowTypeNode) methodEntry.getRetType();
		List<Node> p = arrowNode.getParameters();
		int count = 0;
		if (!(p.size() == parameters.size()))
			throw new TypeException("[ClassCallNode] Wrong number of parameters in the invocation of method " + this.ID);
		
		for(Node par : parameters) {
			
			if ( par instanceof IdNode )
				par = ( ( IdNode ) par).getEntry( ).getRetType( );
			else if ( par instanceof DecNode )
				par = ( ( DecNode )par ).getSymType( );
			else if ( par instanceof CallNode )
				par = ( ( CallNode ) par ).getRetType( );
			else if ( par instanceof ClassCallNode )
				par = ( ( ClassCallNode )par ).getRetType( );
			else par = par.typeCheck( );

			if ( par instanceof ArrowTypeNode )
				par = ( ( ArrowTypeNode )par ).getRetType( );
			
			if (!(FOOLlib.isSubtype( par, ( (ParNode) p.get(count) ).getSymType())))
				throw new TypeException("[ClassCallNode] Wrong type of parameter for method call" );
			count++;
		}
		
		return arrowNode.getRetType();
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
