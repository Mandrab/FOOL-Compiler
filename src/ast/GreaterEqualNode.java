package ast;
import lib.*;

public class GreaterEqualNode implements Node {

  private Node left;
  private Node right;
  
  public GreaterEqualNode (Node l, Node r) {
   left=l;
   right=r;
  }
  
  public String toPrint(String s) {
   return s+"GreaterEqual\n" + left.toPrint(s+"  ")   
                      + right.toPrint(s+"  ") ; 
  }
    
  public Node typeCheck() throws TypeException {
	Node l= left.typeCheck();  
	Node r= right.typeCheck();  
    if ( !(FOOLlib.isSubtype(l, r) || FOOLlib.isSubtype(r, l)) ) 
    	throw new TypeException("Incompatible types in  greaterequal");
    return new BoolTypeNode();
  }
  
  /*Per la code generation uso comunque ble (branch less equal) che effettua il salto se il primo valore è minore uguale 
   * al secondo. Poi adatto le condizioni del salto, ragionando in maniera inversa.
   */
  public String codeGeneration() {
    String l1= FOOLlib.freshLabel();
    String l2= FOOLlib.freshLabel();
    String l3= FOOLlib.freshLabel();
	  return left.codeGeneration()+
			 right.codeGeneration()+
			 "bleq "+l1+"\n"+ //Se è minore uguale salto al label l2 e controllo che sia uguale
			 "push 1\n"+ //se è maggiore pusho 1 (true)
			 "b "+l3+"\n"+ //poi salto direttamente a l3
			 l1+": \n"+ 
			 "beq "+l2+"\n"+  //se è minore uguale, controllo che sia uguale 
			 "push 0\n"+    //se non è uguale, pusho 0 (false) perche vuol dire che il prima valore è minore del secondo
			 "b "+l3+"\n"+  //poi salto direttamente a l3
			 l2+": \n"+	  
			 "push 1\n"+//se è uguale pusho 1 (true)
			 l3+": \n";
  }
  
  
}  