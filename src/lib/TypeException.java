package lib;

public class TypeException extends Exception {
	
	public String text;

	public TypeException (String t) {
		 FOOLlib.typeErrors++;
		 text=t;
    }
		  
	
}
