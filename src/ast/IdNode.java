package ast;

import lib.*;

public class IdNode implements Node {

	private String id;
	private int nestingLevel;
	private STentry entry;

	public IdNode(String i, STentry st, int nl) {
		id = i;
		nestingLevel = nl;
		entry = st;
	}

	public String toPrint(String s) {
		return s + "Id:" + id + "\n" + ((entry != null) ? entry.toPrint(s + "  ") : "");
	}

	public Node typeCheck() throws TypeException {
		return entry.getType();
	}

	/*
	 * ricontrollare possibile errore in getPush
	 */
	public String codeGeneration() {
String getAR="";
		
		for (int i=0;i<nestingLevel-entry.getNestingLevel(); i++ )
			getAR+="lw\n";
		
		if(!(entry.getType() instanceof ArrowTypeNode)){
			return 
					"lfp\n"+	// AL
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
			return  // Salviamo sullo Stack l'FP ad AR dichiarazione funzione. FP del frame dove è dichiarata la funzione.
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