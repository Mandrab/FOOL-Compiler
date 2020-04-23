package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.*;

/**
 * Store useful informations for the compilation process
 * 
 * @author Paolo Baldini
 */
public class FOOLLib {

    public static final int MEMSIZE = 10000;		// memory size of the virtual machine

    private int typeErrors;							// type errors in the code

    private Map<String, String> superType;			// map a class to its super-class
    private List<List<String>> dispatchTables;		// list dispatch tables

	private int labelCount;
	private int funLabelCount;
	private int methodLabelCount;
	private StringBuilder routinesCode;				// code of functions/methods (to be appended at the end of the program's code)
	
	public FOOLLib( ) {
		superType = new HashMap<>( );
		dispatchTables = new ArrayList<>( );
		
		routinesCode = new StringBuilder( );
	}

	/**
	 * Verify if node 'a' is sub-type of node 'b'
	 * Check covariance between return types and contravariance between parameters' type
	 * 
	 * @param 'a':
	 * 		node to check if "is sub-type of" (b)
	 * @param 'b':
	 * 		node to check if "is super-type of" (a)
	 * @return
	 * 		true if 'a' is sub-type of 'b', false otherwise
	 */
	public boolean isSubtype( Node a, Node b ) {

		// if 'a' is a 'container' node, take his value
		if ( a instanceof DecNode )
			a = ( ( DecNode ) a ).getSymType( );

		// if 'b' is a 'container' node, take his value
		if ( b instanceof DecNode )
			b = ( ( DecNode ) b ).getSymType( );

		if ( ( a instanceof ArrowTypeNode ) && ( b instanceof ArrowTypeNode ) ) {
			ArrowTypeNode arrowA = ( ArrowTypeNode ) a;
			ArrowTypeNode arrowB = ( ArrowTypeNode ) b;

			// if different number of parameters, then cannot be sub-type
			if ( arrowA.getParameters( ).size( ) != arrowB.getParameters( ).size( ) )
				return false;

			// if covariance between return type, then check contravariance between parameters
			if ( isSubtype( arrowA.getRetType( ), arrowB.getRetType( ) ) )
				return contravariance( arrowA.getParameters( ), arrowB.getParameters( ) );

			return false;
		}

		// if 'a' is 'null' and 'b' is a reference to class, then 'a' is sub-type of 'b'
		if ( a instanceof EmptyTypeNode )
			return b instanceof RefTypeNode;

		// if b is 'null', then it cannot be super-type of anything
		if ( b instanceof EmptyTypeNode )
			return false;

		// if both are reference to class, then check sub-typing
		if ( a instanceof RefTypeNode && b instanceof RefTypeNode ) {
			String idFirst = ( ( RefTypeNode ) a ).getID( );
			String idSecond = ( ( RefTypeNode ) b ).getID( );

			// check if 'a' (or any of his super-type) is equal to 'b'
			// if no more super-type to check exist (idFirst is null),
			// then 'a' is not sub-type of 'b'
			while( idFirst != null && ! idFirst.equals( idSecond ) )
				idFirst = superType.get( idFirst );

			// if a class is found, then return true, otherwise false
			return idFirst != null;
		}

		// in FOOL language, bool is sub-type of int
		if ( a instanceof BoolTypeNode )
			return b instanceof BoolTypeNode || b instanceof IntTypeNode;

		// if 'a' is int, 'b' must be int
		if ( a instanceof IntTypeNode )
			return b instanceof IntTypeNode;

		return false;
	}

	/**
	 * Allow to use, in the if-then-else node, two expressions also when their return types have a common ancestor
	 * e.g., the expression 'if ( CONDITION ) then { B } else { C }' where:
     *          A
     *        /   \
     *       B     C
     * is allowed
     *
	 * @param 'a' a node of the ast 
	 * @param 'b' a node of the ast 
	 * @return if a 'lowest common ancestor' (between 'a' and 'b') exist,
	 * 		then return its type
	 * 		else return null 
	 */
	public Node lowestCommonAncestor( Node a, Node b ) {

		// if 'a' is 'null', then return 'b'
		if ( ( a instanceof EmptyTypeNode ) )
			return  b; 

		// if 'b' is 'null', then return 'a'
		if ( ( b instanceof EmptyTypeNode ) )
			return a;

		// create the lowest common ancestor of two arrow-type-node which allows it 
		// to be used in place of both
		if ( a instanceof ArrowTypeNode && b instanceof ArrowTypeNode ) {
			ArrowTypeNode arrowA = ( ArrowTypeNode ) a;
			ArrowTypeNode arrowB = ( ArrowTypeNode ) b;

			// if different quantity of parameters, then cannot exist a lowest common ancestor
			if( arrowA.getParameters( ).size( ) != arrowB.getParameters( ).size( ) )
				return null;

			// find ancestor of return type. If it not exist, return null
			Node ancestor = lowestCommonAncestor( arrowA.getRetType( ), arrowB.getRetType( ) );
			if( ancestor == null ) return null;

			// get the least generic type for each parameter and put it in the list
			List<Node> parList = new ArrayList<Node>( );
			for( int i = 0; i < arrowA.getParameters( ).size( ); i++ ) {
				Node aPar = arrowA.getParameters( ).get( i );
				Node bPar = arrowB.getParameters( ).get( i );

				if ( isSubtype( aPar, bPar ) )
					parList.add( aPar );
				else if ( isSubtype( bPar, aPar ) )
					parList.add( bPar );
				else return null;				
			}

			// return the new arrow-type-node
			return new ArrowTypeNode( parList, ancestor );
		}

		// find the class that is super-class of the two
		if ( a instanceof RefTypeNode && b instanceof RefTypeNode ) {

			// if 'b' is sub-type of 'a', then return 'a'
			// otherwise check parent of 'a'
			while ( a != null && ! isSubtype( b, a ) )
				a = new RefTypeNode( superType.get( ( ( RefTypeNode ) a ).getID( ) ) );

			return a;
		}
		
		// if int/bool types, then take the less generic ones
		// TODO check if a more accurate control is needed
		if ( a instanceof BoolTypeNode )
			return b;
		return a;
	}

	/**
	 * Checks that all the nodes of the second list are sub-type of the node of the first list
	 * 
	 * @param aNodes
	 * 		first list of nodes to check if "is super-type of" (the second list)
	 * @param bNodes
	 * 		second list of nodes to check if "is sub-type of" (the first list)
	 * @return
	 * 		true is contravariance exist, false otherwise
	 */
	private boolean contravariance( List<Node> aNodes, List<Node> bNodes ) {

		// for each node of 'a', check is it's super-type of the corresponded node of 'b'
		for ( int i = 0; i < aNodes.size( ); i++ ) {
			Node a = aNodes.get( i );
			Node b = bNodes.get( i );

			if ( ! isSubtype( b, a ) )
				return false;
		}
		return true;
	}
	
	public void incTypeErrors( ) {
		typeErrors++;
	}
	
	public int getTypeErrors( ) {
		return typeErrors;
	}
	
	public void setSuperType( String classID, String superClassID ) {
		superType.put( classID, superClassID );
	}

	public Map<String, String> getSuperType( ) {
		return superType;
	}
	
	public void addDispatchTable( List<String> dispatchTable ) {
		dispatchTables.add( dispatchTable );
	}

	public List<List<String>> getDispatchTables( ) {
		return dispatchTables;
	}
	
	public List<String> getDispatchTable( int idx ) {
		return dispatchTables.get( idx );
	}
	
	public String freshLabel( ) {
		return "label" + labelCount++;
	}

	public String freshFunctionLabel( ) {
		return "function" + funLabelCount++;
	}
	
	public String freshMethodLabel( ) {
		return "method" + methodLabelCount++;
	}

	public void putCode( String code ) {
		routinesCode.append( "\n" + code );
	}

	public String getCode( ) {
		return routinesCode.toString( );
	}
}