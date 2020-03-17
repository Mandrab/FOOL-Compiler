package ast;
import lib.*;

public class ProgNode implements Node {

  private Node exp;
  
  public ProgNode (Node e) {
   exp=e;
  }
  
  public String toPrint(String s) {
    
   return s+"Prog\n" + exp.toPrint(s+"  ") ;
  }
  
  public Node typeCheck() throws TypeException {
	    return exp.typeCheck();
  }
    
  public String codeGeneration() {
	  return exp.codeGeneration()+
			 "halt\n";
  }
}  