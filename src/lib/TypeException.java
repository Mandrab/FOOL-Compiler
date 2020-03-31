package lib;

public class TypeException extends RuntimeException {
	
	private static final long serialVersionUID = -4973176543505016805L;
	
	private String text;

	private TypeException( String msg ) {
		 text = msg;
    }
	
	@Override
	public String getMessage( ) {
		return text;
	}
	
	public static TypeException build( String msg ) {
		 return new TypeException( msg );
	}
	
	public static TypeException buildAndMark( String msg, FOOLLib lib ) {
		 lib.incTypeErrors( );
		 return new TypeException( msg );
	}
}
