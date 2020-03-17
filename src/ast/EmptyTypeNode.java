package ast;

/*Tipo null. Utilizzato da EmptyNode come tipo di ritorno.*/
public class EmptyTypeNode implements Node {
	  
	  public EmptyTypeNode () {
	  }
	  
	  public String toPrint(String s) {
	   return s+"EmptyType\n";  
	  }
	  
	  //non utilizzato
	  public Node typeCheck() {return null;}
	 
	  //non utilizzato
	  public String codeGeneration() {return "";}
}
