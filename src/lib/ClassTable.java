package lib;
import java.util.HashMap;
import java.util.Map;

import ast.STentry;

public class ClassTable {
	
	private Map<String, Map<String, STentry>> classTable;
	
	public ClassTable( ) {
		classTable = new HashMap<>( );
	}
	
	public Map<String, STentry> addClassVT( String classID, Map<String, STentry> virtualTable ) {
		return classTable.put( classID, virtualTable );
	}
	
	public Map<String, STentry> getClassVT( String classID ) {
		return classTable.get( classID );
	}

}
