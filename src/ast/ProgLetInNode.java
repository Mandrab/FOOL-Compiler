package ast;

import lib.*;
import java.util.List;

public class ProgLetInNode implements Node {

  private List<Node> declist;
  private Node exp;
  
  public ProgLetInNode (List<Node> d, Node e) {
   declist=d;
   exp=e;
  }
  
  public String toPrint(String s) {
	 String declstr="";
	 for (Node dec:declist) declstr+=dec.toPrint(s+"  ");
     return s+"ProgLetIn\n" + declstr + exp.toPrint(s+"  ") ; 
  }

  public Node typeCheck() throws TypeException {
    for (Node dec:declist)
	    try {
	      dec.typeCheck(); } 
        catch (TypeException e) {
          System.out.println("Type checking error in a declaration: "+e.text); }  	
    return exp.typeCheck();
  }
    
  public String codeGeneration() {
	  String declCode="";
	  for (Node dec:declist)
		  declCode += dec.codeGeneration();
	  return "push 0\n"+
			 declCode+
			 exp.codeGeneration()+
			 "halt\n" +
			 FOOLlib.getCode();
  }
}  