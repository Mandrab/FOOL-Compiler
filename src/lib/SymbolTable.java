package lib;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.STEntry;

public class SymbolTable {

	private List<Map<String, STEntry>> symTable;
	private int nestingLevel;
	
	public SymbolTable( ) {
		symTable = new ArrayList<Map<String,STEntry>>( );
		nestingLevel = -1;
	}
	
	public Map<String, STEntry> nestTable( ) {
		Map<String, STEntry> level = new HashMap<String, STEntry>( );
		symTable.add( level );
		nestingLevel++;
		
		return level;
	}
	
	public Map<String, STEntry> getTable( ) {
		return getTable( nestingLevel );
	}
	
	public Map<String, STEntry> getTable( int nestingLevel ) {
		if ( nestingLevel > symTable.size( ) ) throw new IllegalStateException( );
		
		return symTable.get( nestingLevel );
	}
	
	public Map<String, STEntry> popTable( ) {
		if ( nestingLevel > symTable.size( ) ) throw new IllegalStateException( );
		
		return symTable.remove( nestingLevel-- );
	}
	
	public int getLevel( ) {
		return nestingLevel;
	}
}
