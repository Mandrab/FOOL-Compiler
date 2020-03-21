package ast;

import visitors.Visitor;

/*Tipo null. Utilizzato da EmptyNode come tipo di ritorno.*/
public class EmptyTypeNode implements Node {

	@Override
	public <T> T accept( Visitor<T> visitor ) {
		return visitor.visit( this );
	}

	
	
	
	
	  
	  //non utilizzato
	  public Node typeCheck() {return null;}
	 
	  //non utilizzato
	  public String codeGeneration() {return "";}
}
