package ast;
import lib.*;

public class TimesNode implements Node {

  private Node left;
  private Node right;
  
  public TimesNode (Node l, Node r) {
   left=l;
   right=r;
  }
  
  public String toPrint(String s) {
   return s+"Times\n" + left.toPrint(s+"  ")  
                      + right.toPrint(s+"  "); 
  }

  public Node typeCheck() throws TypeException {
   if ( ! ( FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode()) &&
		    FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode()) ) ) 
	   throw new TypeException("Non integers in multiplication");
   return new IntTypeNode();
  }
  
  public String codeGeneration() {
	  return left.codeGeneration()+right.codeGeneration()+"mult\n";
  }

}  