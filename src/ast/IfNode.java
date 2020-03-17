package ast;
import lib.*;

public class IfNode implements Node {

  private Node cond;
  private Node th;
  private Node el;
  
  public IfNode (Node c, Node t, Node e) {
   cond=c;
   th=t;
   el=e;
  }
  
  public String toPrint(String s) {
   return s+"If\n" + cond.toPrint(s+"  ") 
                 + th.toPrint(s+"  ")   
                 + el.toPrint(s+"  ") ; 
  }

  public Node typeCheck() throws TypeException {
	if ( !(FOOLlib.isSubtype(cond.typeCheck(), new BoolTypeNode())) ) 
		throw new TypeException("Non boolean condition in if");
	Node t= th.typeCheck();  
	Node e= el.typeCheck();  
	if (FOOLlib.isSubtype(t, e))
      return e;
	if (FOOLlib.isSubtype(e, t))
	  return t;
	throw new TypeException("Incompatible types in then-else branches");
  }
  
  public String codeGeneration() {
	    String l1= FOOLlib.freshLabel();
	    String l2= FOOLlib.freshLabel();
		  return cond.codeGeneration()+
				 "push 1\n"+
				 "beq "+l1+"\n"+				 				  
				 el.codeGeneration()+
				 "b "+l2+"\n"+
				 l1+": \n"+
				 th.codeGeneration()+
				 l2+": \n";	         
  }
}  