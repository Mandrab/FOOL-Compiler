package lib;

import java.util.HashMap;
import java.util.Map;

import ast.*;

public class FOOLlib {

	public static int typeErrors = 0;
	public static Map<String, String> superType = new HashMap<>();

	/***
	 * Valuta se il tipo "a" è <= al tipo "b"
	 * Valuta co-varianza tra tipi di ritorno e controvarianza sui tipi del parametri.
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isSubtype(Node a, Node b) {

		if ((a instanceof ArrowTypeNode) && (b instanceof ArrowTypeNode)) {
			if (((ArrowTypeNode) a).getParList().size() != ((ArrowTypeNode) b).getParList().size()) {
				return false;
			} else {
				if (!(((ArrowTypeNode) a).getRet().getClass().equals(((ArrowTypeNode) b).getRet().getClass())
						|| ((((ArrowTypeNode) a).getRet() instanceof BoolTypeNode)
								&& (((ArrowTypeNode) b).getRet() instanceof IntTypeNode)))) {
					return false;
				} else {
					for (int i = 0; i < ((ArrowTypeNode) a).getParList().size(); i++) {
						if (!((((ArrowTypeNode) a).getParList().get(i).getClass()
								.equals(((ArrowTypeNode) b).getParList().get(i).getClass())
								|| ((((ArrowTypeNode) a).getParList().get(i) instanceof IntTypeNode)
										&& (((ArrowTypeNode) b).getParList().get(i) instanceof BoolTypeNode))))) {
							return false;
						}

					}
					return true;
				}
			}
		}
		
		//a Empty --> b RefType TRUE
		//a Empty --> b != RefType FALSE
		if((a instanceof EmptyTypeNode)) {
			return  (b instanceof RefTypeNode);
		}
		//b Empty --> FALSE
		if(b instanceof EmptyTypeNode) {
			return false;
		}
		
		if ((a instanceof RefTypeNode) && (b instanceof RefTypeNode)) {
			String idFirst = ((RefTypeNode) a).getID();
			String idSecond = ((RefTypeNode) b).getID();
			
			//Risalgo a, se trovo un predecessore = b allora è sottotipo,
			//se arrivo alla classe padre di tutti senza trovare un uguaglianza verificata
			//concludo che a non è sottotipo di b
			while(!(idSecond.equals(idFirst)) && idFirst != null) {
				idFirst = superType.get(idFirst);
			}
			return (idFirst != null);
		}

		return a.getClass().equals(b.getClass()) || ((a instanceof BoolTypeNode) && (b instanceof IntTypeNode));
	}
	
	/***
	 *OTTIMIZZAZIONE 3) Type Checking per IF-THEN-ELSE 
	 * @param a
	 * @param b
	 * @return
	 */
	public Node lowestCommonAncestor(Node a, Node b) {
		//se sono uguali ritorno 
		
		
		//Se uno dei due è empty ritorno l'altro
		if((a instanceof EmptyTypeNode))
			return  b; 
		
		if((b instanceof EmptyTypeNode))
			return a;
		
		//Int/Bool return il più grande
		
		
		//Altrimenti devo cercare il lowest common ancestor, risalendo la catena:
		//se b è sottotipo di a -> return a
		//altrimenti risalgo il padre di a e controllo
		
		
		//se non ho ancora restituito niente: null
		return null;
	}

	private static int labCount = 0;

	public static String freshLabel() {
		return "label" + (labCount++);
	}

	private static int funlabCount = 0;

	public static String freshFunLabel() {
		return "function" + (funlabCount++);
	}

	private static String funCode = "";

	public static void putCode(String c) {
		funCode += "\n" + c; // aggiunge una linea vuota di separazione prima di funzione
	}

	public static String getCode() {
		return funCode;
	}

}
   

