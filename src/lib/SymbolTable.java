package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.STEntry;

/**
 * Contains tables of definitions in nested scopes
 * 
 * @author Paolo Baldini
 */
public class SymbolTable {

	private List<Map<String, STEntry>> symTable;
	private int nestingLevel;

	public SymbolTable( ) {
		symTable = new ArrayList<Map<String,STEntry>>( );
		nestingLevel = -1;
	}

	/**
	 * Add and return a new nested table
	 * 
	 * @return
	 * 		the nested table
	 */
	public Map<String, STEntry> nestTable( ) {
		Map<String, STEntry> level = new HashMap<String, STEntry>( );
		symTable.add( level );
		nestingLevel++;

		return level;
	}

	/**
	 * From the symbol tables, get the most deeply nested table
	 * 
	 * @return
	 * 		the most deeply nested table
	 */
	public Map<String, STEntry> getTable( ) {
		return getTable( nestingLevel );
	}

	/**
	 * Get the table at the specified nesting level
	 * 
	 * @param nestingLevel
	 * 		required nesting level
	 * @return
	 * 		return the required table
	 * @throws IllegalStateException
	 * 		if required nesting level table doesn't exist
	 */
	public Map<String, STEntry> getTable( int nestingLevel ) {
		if ( nestingLevel >= symTable.size( ) ) throw new IllegalStateException( );

		return symTable.get( nestingLevel );
	}

	/**
	 * Remove the most deeply nested table
	 * 
	 * @return
	 * 		the removed table
	 * @throws IllegalStateException
	 * 		if there isn't any table to remove
	 */
	public Map<String, STEntry> popTable( ) {
		if ( nestingLevel >= symTable.size( ) ) throw new IllegalStateException( );

		return symTable.remove( nestingLevel-- );
	}

	/**
	 * Get actual nesting level
	 * 
	 * @return
	 * 		the actual nesting level
	 */
	public int getLevel( ) {
		return nestingLevel;
	}

}
