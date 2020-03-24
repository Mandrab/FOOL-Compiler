package lib;

public class TypeException extends RuntimeException {
	
	private static final long serialVersionUID = -4973176543505016805L;
	
	private String text;

	public TypeException( String t ) {
		 FOOLlib.typeErrors++;
		 text = t;
    }
	
	@Override
	public String getMessage( ) {
		return text;
	}
}
