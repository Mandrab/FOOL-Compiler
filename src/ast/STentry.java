package ast;

public class STentry {
   
  private int nl;
  private Node type;
  private int offset;
  private boolean isMethod;

  public STentry (int n, Node t, int o, boolean isMethod) {
	  nl=n;
	  type=t;
	  offset=o;
	  this.isMethod = isMethod;
  }
  
  public Node getType() {
	  return type;
  }
  
  public int getOffset() {
	  return offset;
  }
  
  public int getNestingLevel() {
	  return nl;
  }
  
  public String toPrint(String s) {
	   return s+"STentry: nestlev " + nl +"\n"+
			  s+"STentry: type\n " +
			      type.toPrint(s+"  ") +
			  s+"STentry: offset " + offset +"\n";
  }
  
  public boolean isMethod() {
	  return this.isMethod;
  }
  
}  