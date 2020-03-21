package ast;

import java.util.ArrayList;
import java.util.List;

import lib.FOOLlib;
import lib.TypeException;
import visitors.Visitor;

public class MethodNode implements DecNode, Node {

	private String ID;
	private Node returnType;
	private List<Node> parameters; // campo "parameters" che e' lista di Node
	private List<Node> declarations = new ArrayList<Node>(); //let in
	private Node exp;
	private int offset;
	private String generatedLabel;

	public MethodNode( String id, Node returnType ) {
		this.ID = id;
		this.returnType = returnType;
		this.parameters = new ArrayList<Node>( );
		this.declarations = new ArrayList<Node>( );
		this.offset = -1;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public Node getSymType( ) {
		return returnType;
	}
	
	public void addParameter( Node parameter ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		parameters.add( parameter );
	}
	
	public List<Node> getParameters( ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		return parameters;
	}
	
	public void addDeclaration( Node declaration ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		declarations.add( declaration );
	}
	
	public List<Node> getDeclarations( ) { // metodo "addPar" che aggiunge un nodo a campo "parameters"
		return declarations;
	}
	
	public void setExpession( Node expression ) {
		exp = expression;
	}
	
	public Node getExpession( ) {
		return exp;
	}
	
	public void setOffset( int offset ) {
		this.offset = offset;
	}
	
	public int getOffset( ) {
		return offset;
	}

	public String getLabel( ) {
		return generatedLabel;
	}

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	



	

	



	public Node typeCheck() throws TypeException {
		for (Node dec : declarations)
			try {
				dec.typeCheck();
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		
		if (!FOOLlib.isSubtype(exp.typeCheck(), returnType))
			throw new TypeException("[MethodNode] Wrong return type for method " + ID);
		return null;
	}

	public String codeGeneration() {
		String declCode = "";
		String remdeclCode = "";
		String parCode = "";
		this.generatedLabel = FOOLlib.freshMethodLabel();
		

		// Nel caso di elementi funzionali aggiungiamo un "pop" aggiuntivo.

		for (int i = 0; i < declarations.size(); i++) {
			if (declarations.get(i) instanceof MethodNode) {
				remdeclCode += "pop\n";					//pop del codice dichiarazione se funzionale
			}
			declCode += declarations.get(i).codeGeneration(); //codice delle dichiarazioni
			remdeclCode += "pop\n";						//pop del codice dichiarazione
		}		

		for (int i = 0; i < parameters.size(); i++) {
			if (((DecNode) parameters.get(i)).getSymType() instanceof ArrowTypeNode) {
				parCode += "pop\n";					//pop dei parametri se funzionale
			}
			parCode += "pop\n";						//pop dei parametri
		}

		FOOLlib.putCode(
				this.generatedLabel + ":\n" + 
				"cfp\n" + // setta il registro $fp / copy stack pointer into frame pointer
				"lra\n" + // load from ra sullo stack
				declCode + // codice delle dichiarazioni
				exp.codeGeneration() + "stm\n" + // salvo il risultato in un registro
				remdeclCode + // devo svuotare lo stack, e faccio pop tanti quanti sono le var/fun dichiarate
				"sra\n" + // salvo il return address
				"pop\n" + // pop dell'AL (access link)
				parCode + // pop dei parametri che ho in parameters
				"sfp\n" + // ripristino il registro $fp al CL, in maniera che sia l'fp dell'AR del
							// chiamante.
				"ltm\n" + "lra\n" + "js\n" // js salta all'indirizzo che � in cima allo stack e salva la prossima
											// istruzione in ra.
		);
			
		return "";
	}

	

	
	
}
