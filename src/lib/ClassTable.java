package lib;
import java.util.HashMap;
import java.util.Map;

import ast.STEntry;

/**
 * Contains the definition of fields and methods of each class
 */
public class ClassTable {

	private Map<String, Map<String, STEntry>> classTable;

	public ClassTable( ) {
		classTable = new HashMap<>( );
	}

	public Map<String, STEntry> addClassVT( String classID, Map<String, STEntry> virtualTable ) {
		return classTable.put( classID, virtualTable );
	}

	public Map<String, STEntry> getClassVT( String classID ) {
		return classTable.get( classID );
	}

}
