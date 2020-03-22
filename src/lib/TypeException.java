package lib;

public class TypeException extends RuntimeException {
	
	private static final long serialVersionUID = -4973176543505016805L;
	
	public String text;

	public TypeException (String t) {
		 FOOLlib.typeErrors++;
		 text=t;
    }
}
