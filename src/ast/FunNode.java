package ast;

import java.util.ArrayList;
import java.util.List;

import lib.*;

public class FunNode implements Node, DecNode {

	private String id;
	private Node type;
	private List<Node> parlist = new ArrayList<Node>(); // campo "parlist" che � lista di Node
	private List<Node> declist = new ArrayList<Node>();
	private Node exp;

	public FunNode(String i, Node t) {
		id = i;
		type = t;
	}

	public void addDec(List<Node> d) {
		declist = d;
	}

	public void addBody(Node b) {
		exp = b;
	}

	public void addPar(Node p) { // metodo "addPar" che aggiunge un nodo a campo "parlist"
		parlist.add(p);
	}
	
	public List<Node> getPars() { // metodo "addPar" che aggiunge un nodo a campo "parlist"
		return parlist;
	}

	public String toPrint(String s) {
		String parlstr = "";
		for (Node par : parlist)
			parlstr += par.toPrint(s + "  ");
		String declstr = "";
		for (Node dec : declist)
			declstr += dec.toPrint(s + "  ");
		return s + "Fun:" + id + "\n" + type.toPrint(s + "  ") + parlstr + declstr + exp.toPrint(s + "  ");
	}

	public Node typeCheck() throws TypeException {
		for (Node dec : declist)
			try {
				dec.typeCheck();
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		if (!FOOLlib.isSubtype(exp.typeCheck(), type))
			throw new TypeException("Wrong return type for function " + id);
		return null;
	}

	public String codeGeneration() {
		String declCode = "";
		String remdeclCode = "";
		String parCode = "";
		String funl = FOOLlib.freshFunLabel();

		

		// Nel caso di elementi funzionali aggiungiamo un "pop" aggiuntivo.

		for (int i = 0; i < declist.size(); i++) {
			if (declist.get(i) instanceof FunNode) {
				remdeclCode += "pop\n";					//pop del codice dichiarazione se funzionale
			}
			declCode += declist.get(i).codeGeneration(); //codice delle dichiarazioni
			remdeclCode += "pop\n";						//pop del codice dichiarazione
		}		

		for (int i = 0; i < parlist.size(); i++) {
			if (((DecNode) parlist.get(i)).getSymType() instanceof ArrowTypeNode) {
				parCode += "pop\n";					//pop dei parametri se funzionale
			}
			parCode += "pop\n";						//pop dei parametri
		}

		FOOLlib.putCode(funl + ":\n" + "cfp\n" + // setta il registro $fp / copy stack pointer into frame pointer
				"lra\n" + // load from ra sullo stack
				declCode + // codice delle dichiarazioni
				exp.codeGeneration() + "stm\n" + // salvo il risultato in un registro
				remdeclCode + // devo svuotare lo stack, e faccio pop tanti quanti sono le var/fun dichiarate
				"sra\n" + // salvo il return address
				"pop\n" + // pop dell'AL (access link)
				parCode + // pop dei parametri che ho in parlist
				"sfp\n" + // ripristino il registro $fp al CL, in maniera che sia l'fp dell'AR del
							// chiamante.
				"ltm\n" + "lra\n" + "js\n" // js salta all'indirizzo che � in cima allo stack e salva la prossima
											// istruzione in ra.
		);

		return "lfp\n" + "push " + funl + "\n";
	}

	@Override
	public Node getSymType() {
		return type;
	}

}