package visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ast.Node;

public class ReflectionVisitor<T> {

	@SuppressWarnings("unchecked")
	public T visit( Node element ) throws InvocationTargetException {
		try {
			Method m = getClass( ).getMethod( "visit", new Class[] { element.getClass( ) } );
			return ( T ) m.invoke( this, element );
		} catch ( IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e ) {
			e.printStackTrace( );
		}
		return null;
	}

}
