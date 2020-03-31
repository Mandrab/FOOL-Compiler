package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.*;

public class FOOLLib {

    public static final int MEMSIZE = 10000;

    private int typeErrors;
    private Map<String, String> superType;
    private List<List<String>> dispatchTables;

	private int labCount;
	private int funlabCount;
	private int methodlabCount;
	private StringBuilder funCode;
	
	public FOOLLib( ) {
		superType = new HashMap<>( );
		dispatchTables = new ArrayList<>( );
		
		funCode = new StringBuilder( );
	}

	/***
	 * Verify if node 'a' is sub-type of node 'b'
	 * Check covariance between return types and contravariance between parameters' type
	 * @param 'a':
	 * 		node to check if "is sub-type of" (b)
	 * @param 'b':
	 * 		node to check if "is super-type of" (a)
	 * @return
	 * 		true if 'a' is sub-type of 'b'
	 * 		false otherwise
	 */
	public boolean isSubtype( Node a, Node b ) {

		if ( ( a instanceof ArrowTypeNode ) && ( b instanceof ArrowTypeNode ) ) {
			if ( ( ( ArrowTypeNode ) a ).getParameters( ).size( ) != ( ( ArrowTypeNode ) b ).getParameters( ).size( ) ) {
				return false;
			} else {
				if ( ! ( ( ( ArrowTypeNode ) a ).getRetType( ).getClass( ).equals( ( ( ArrowTypeNode ) b ).getRetType( ).getClass( ) )
						|| ( ( ( ( ArrowTypeNode ) a ).getRetType( ) instanceof BoolTypeNode )
								&& ( ( ( ArrowTypeNode ) b ).getRetType( ) instanceof IntTypeNode ) ) ) ) {
					return false;
				} else {
					for ( int i = 0; i < ( ( ArrowTypeNode ) a ).getParameters( ).size( ); i++ ) {
						if ( ! ( ( ( ( ArrowTypeNode ) a ).getParameters( ).get( i ).getClass( )
								.equals( ( ( ArrowTypeNode ) b ).getParameters( ).get( i ).getClass( ) )
								|| ( ( ( ( ArrowTypeNode ) a ).getParameters( ).get( i ) instanceof IntTypeNode )
										&& ( ( ( ArrowTypeNode ) b ).getParameters( ).get( i ) instanceof BoolTypeNode ) ) ) ) ) {
							return false;
						}
					}
					return true;
				}
			}
		}
		
		//a Empty --> b RefType TRUE
		//a Empty --> b != RefType FALSE
		if( ( a instanceof EmptyTypeNode ) ) {
			return  ( b instanceof RefTypeNode );
		}
		//b Empty --> FALSE
		if( b instanceof EmptyTypeNode ) {
			return false;
		}
		
		if ( ( a instanceof RefTypeNode ) && ( b instanceof RefTypeNode ) ) {
			String idFirst = ( ( RefTypeNode ) a ).getID( );
			String idSecond = ( ( RefTypeNode ) b ).getID( );
			
			//Risalgo a, se trovo un predecessore = b allora � sottotipo,
			//se arrivo alla classe padre di tutti senza trovare un uguaglianza verificata
			//concludo che a non � sottotipo di b
			while( ! ( idSecond.equals( idFirst ) ) && idFirst != null ) {
				idFirst = superType.get( idFirst );
			}
			return ( idFirst != null );
		}

		return a.getClass( ).equals( b.getClass( ) ) || ( ( a instanceof BoolTypeNode ) && ( b instanceof IntTypeNode ) );
	}
	
	/***
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
		//se sono uguali ritorno 
		//Se uno dei due e empty ritorno l'altro
		if( ( a instanceof EmptyTypeNode ) )
			return  b; 
		
		if( ( b instanceof EmptyTypeNode ) )
			return a;
		
		if( a instanceof ArrowTypeNode && b instanceof ArrowTypeNode ) {
			ArrowTypeNode arrowA = ( ArrowTypeNode )a;
			ArrowTypeNode arrowB = ( ArrowTypeNode )b;
			
			if( arrowA.getParameters( ).size( ) != arrowB.getParameters( ).size( ) ) {
				return null;
			}
			Node lCommonAncestor = lowestCommonAncestor( arrowA.getRetType( ), arrowB.getRetType( ) );
			if( lCommonAncestor == null ) {
				return lCommonAncestor;
			}
			List<Node> parList = new ArrayList<Node>( );
			for( int i = 0; i < arrowA.getParameters( ).size( ); i++ ) {
				if( ! ( isSubtype( arrowA.getParameters( ).get( i ), arrowB.getParameters( ).get( i ) ) ) && 
						! ( isSubtype( arrowB.getParameters( ).get( i ), arrowA.getParameters( ).get( i ) ) ) ){
					return null;
				}
				else if( isSubtype( arrowA.getParameters( ).get( i ), arrowB.getParameters( ).get( i ) ) ) {
					parList.add( arrowA.getParameters( ).get( i ) );
				}
				else {
					parList.add( arrowB.getParameters( ).get( i ) );
				}
				
			}
			return new ArrowTypeNode( parList, lCommonAncestor );
		}
		
		//Altrimenti devo cercare il lowest common ancestor, risalendo la catena:
		//se b e sottotipo di a -> return a
		//altrimenti risalgo il padre di a e controllo		
		if( a instanceof RefTypeNode && b instanceof RefTypeNode ) {
            String idA = ( ( RefTypeNode ) a ).getID( );        
            RefTypeNode parentA = new RefTypeNode( superType.get( idA ) );
            while( parentA != null ) {
                if( isSubtype( b, parentA ) )
                    return parentA;
                    
                idA = superType.get( idA );
                parentA = new RefTypeNode( superType.get( idA ) );
            }		
		}
		
		//Int/Bool return il piu grande
        if( ( a instanceof BoolTypeNode ) && ( b instanceof BoolTypeNode ) )
            return new BoolTypeNode( );
        
        if( ( a instanceof BoolTypeNode ) && ( b instanceof IntTypeNode )|| 
                ( a instanceof IntTypeNode ) && ( b instanceof BoolTypeNode )||
                ( a instanceof IntTypeNode ) && ( b instanceof IntTypeNode ) )
            return new IntTypeNode( );

		//se non ho ancora restituito niente: null
		return null;
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
		return "label" + labCount++;
	}

	public String freshFunctionLabel( ) {
		return "function" + funlabCount++;
	}
	
	public String freshMethodLabel( ) {
		return "method" + methodlabCount++;
	}

	public void putCode( String code ) {
		funCode.append( "\n" + code );// aggiunge una linea vuota di separazione prima di funzione
	}

	public String getCode( ) {
		return funCode.toString( );
	}

}
   

