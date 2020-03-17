import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.STentry;

public class SymbolTable {

	private List<Map<String, STentry>> symTable;
	private int nestingLevel;
	
	public SymbolTable( ) {
		symTable = new ArrayList<Map<String,STentry>>( );
	}
	
	public Map<String, STentry> nestTable( ) {
		Map<String, STentry> level = new HashMap<String, STentry>( );
		symTable.add( level );
		nestingLevel++;
		
		return level;
	}
	
	public Map<String, STentry> getTable( ) {
		return getTable( nestingLevel );
	}
	
	public Map<String, STentry> getTable( int nestingLevel ) {
		if ( nestingLevel > symTable.size( ) ) throw new IllegalStateException( );
		
		return symTable.get( nestingLevel );
	}
	
	public Map<String, STentry> popTable( ) {
		if ( nestingLevel > symTable.size( ) ) throw new IllegalStateException( );
		
		return symTable.remove( nestingLevel-- );
	}
	
	public int getLevel( ) {
		return nestingLevel;
	}
}
