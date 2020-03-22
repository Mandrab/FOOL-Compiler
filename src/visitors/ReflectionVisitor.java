package visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ast.Node;
import lib.TypeException;

public class ReflectionVisitor<T> {

	@SuppressWarnings("unchecked")
	public T visit( Node element ) {
		try {
			Method m = getClass( ).getMethod( "visit", new Class[] { element.getClass( ) } );
			return ( T ) m.invoke( this, element );
		} catch ( InvocationTargetException ie ) {
			if ( ie.getCause( ) != null )
				if ( ie.getCause( ) instanceof TypeException )
					System.out.println( "TypeException: " + ( ( TypeException ) ie.getCause( ) ).text );
				else ie.getCause( ).printStackTrace( );
		} catch ( Exception e ) {
			throw new RuntimeException( "Visit method does not exist for class " + element.getClass( ) );
		}
		return null;
	}
	
}
