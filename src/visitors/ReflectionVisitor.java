package visitors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ast.Node;

/**
 * Class that uses reflection to call the correct 'visit' method
 * 
 * @author Paolo Baldini
 * 
 * @param <T>
 * 		expected return type of 'visit' method
 */
public class ReflectionVisitor<T> {

	@SuppressWarnings("unchecked")
	public T visit( Node element ) throws InvocationTargetException {
		try {
			// invoke correct visit method (based on parameter; checked at runtime)
			Method m = getClass( ).getMethod( "visit", new Class[] { element.getClass( ) } );
			return ( T ) m.invoke( this, element );
		} catch ( IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e ) {
			e.printStackTrace( );
		}
		return null;
	}

}
