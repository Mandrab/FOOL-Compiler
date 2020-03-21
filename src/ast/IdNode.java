package ast;

import lib.*;
import visitors.Visitor;

public class IdNode implements Node {

	private String ID;
	private STentry entry;
	private int nestingLevel;

	public IdNode( String id, STentry stEntry, int nestingLevel ) {
		this.ID = id;
		this.nestingLevel = nestingLevel;
		this.entry = stEntry;
	}
	
	public String getID( ) {
		return ID;
	}
	
	public STentry getEntry( ) {
		return entry;
	}
	
	public int getNestingLevel() {
		return nestingLevel;
	}
	
	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}
	
	
	
	
	
	




	public Node typeCheck() throws TypeException {
		if(entry.getRetType() instanceof ClassTypeNode)
			throw new TypeException(" Type check found a problem: \n ID can not be a class's name: " + this.ID);
		if(entry.isMethod())
			throw new TypeException(" Type check found a problem: \n ID can not be a method: " + this.ID);
		return entry.getRetType();
	}

	/*
	 * ricontrollare possibile errore in getPush
	 */
	public String codeGeneration() {
		String getAR="";
		
		for (int i=0;i<nestingLevel-entry.getNestingLevel(); i++ )
			getAR+="lw\n";
		
		if(!(entry.getRetType() instanceof ArrowTypeNode)){
			return "lfp\n"+	// AL
					getAR+		// Andiamo nel suo AR. getAr ci da l'AL.
					"push "+entry.getOffset()+"\n"+	// e aggiungiamo 
					"add\n"+
					"lw\n";
		}
		else {
			/* ArrowTypeNode
			 * qualsiasi ID con tipo funzionale (vero ID di funzione oppure
			 * ID di variabile o parametro di tipo funzione) occupa un offset doppio:
			 * [a offset messo in symbol table  ] FP ad AR dichiarazione funzione
			 * [a offset messo in symbol table-1] indir funzione (per invocazione suo codice)
			 */
			return  // Salviamo sullo Stack l'FP ad AR dichiarazione funzione. FP del frame dove � dichiarata la funzione.
					"lfp\n"+
					getAR+		// Andiamo nel suo AR. getAR ci da l'AL.
					"push "+entry.getOffset()+"\n"+
					"add\n"+
					"lw\n"+
					// Salviamo ora sullo Stack l'indir della funzione (per invocazione del suo codice). La sua label.
					"lfp\n"+
					getAR+		// Andiamo nel suo AR. getAR ci da l'AL.
					"push "+entry.getOffset()+"\n"+
					"push 1\n"+
					"sub\n"+
					"add\n"+
					"lw\n";	// Mettiamo sullo stack l'indirizzo della funzione (di nouvo). Ma non sono sicuro.
		}
	}
}