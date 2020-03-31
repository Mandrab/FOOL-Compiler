package visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.AndNode;
import ast.ArrowTypeNode;
import ast.BoolNode;
import ast.BoolTypeNode;
import ast.CallNode;
import ast.ClassCallNode;
import ast.ClassNode;
import ast.ClassTypeNode;
import ast.DivNode;
import ast.EmptyNode;
import ast.EqualNode;
import ast.FieldNode;
import ast.FunNode;
import ast.GreaterEqualNode;
import ast.IdNode;
import ast.IfNode;
import ast.IntNode;
import ast.IntTypeNode;
import ast.LessEqualNode;
import ast.MethodNode;
import ast.MinusNode;
import ast.NewNode;
import ast.Node;
import ast.NotNode;
import ast.OrNode;
import ast.ParNode;
import ast.PlusNode;
import ast.PrintNode;
import ast.ProgLetInNode;
import ast.ProgNode;
import ast.RefTypeNode;
import ast.STEntry;
import ast.TimesNode;
import ast.VarNode;
import generated.FOOLBaseVisitor;
import generated.FOOLParser;
import lib.ClassTable;
import lib.FOOLLib;
import lib.SymbolTable;

public class ParserVisitor extends FOOLBaseVisitor<Node> {

	private final FOOLLib lib;
	private final SymbolTable symTable;
	private final ClassTable classTable;
	
	private int stErrors;
	
	private int offset;
	
	public ParserVisitor( FOOLLib globalLib ) {
		lib = globalLib;
		symTable = new SymbolTable( );
		classTable = new ClassTable( );
		offset = -2;
	}
	
	public int getSymbolTableError( ) {
		return stErrors;
	}
	
	@Override
	public Node visitProg(FOOLParser.ProgContext ctx) {
		// creating first symbol's table
		symTable.nestTable( );

		// creating a list for declarations (class / fun / var)
		List<Node> declarations = new ArrayList<Node>( );

		// add every class declaration (there may be no declarations)
		for ( int i = 0; ctx.cls( ) != null && i < ctx.cls( ).size( ); i++ )
			declarations.add( visit( ctx.cls( i ) ) );

		// add every function / var declaration (there may be no declarations)
		for ( int i = 0; ctx.dec( ) != null && i < ctx.dec( ).size( ); i++ )
			declarations.add( visit( ctx.dec( i ) ) );

		// set program's main expression
		Node exp = visit( ctx.exp( ) );
		
		// now the program should be correctly parsed -> remove symbol's table
		symTable.popTable( );
		
		// if the program has only an expression (without declarations) -> return a ProgNode;
		// otherwise -> return a ProgLetInNode
		return ctx.LET( ) != null ? new ProgLetInNode( declarations,  exp ) : new ProgNode( exp );
	}
	
	@Override
	public Node visitCls(FOOLParser.ClsContext ctx) {
		// get class name
		String clsID = ctx.clsID.getText( );
		// define (optional) superclass type ('extends' case)
		ClassTypeNode superClassType = null;
	   	
		// create classTypeNode (specify class structure)
	   	ClassTypeNode clsTypeNode = new ClassTypeNode( );
	   	// create class node (contains also methods' implementations)
		ClassNode clsNode = new ClassNode( clsTypeNode, ctx.clsID.getText( ) );

		// get last symbol table (where put class declaration)
		Map<String, STEntry> stFront = symTable.getTable( );
		
		// add class declaration to symTable
		if ( stFront.put( clsID, new STEntry( symTable.getLevel( ), clsTypeNode, offset--, false ) ) != null ) {
	        System.out.println( "Class ID '" + clsID + "' at line " + ctx.clsID.getLine( ) + " already declared" );
	    	stErrors++;
	    }

		// create a nested table for class' declarations
      	Map<String,STEntry> stClsNestedLevel = symTable.nestTable( );
      	
      	// set fields and methods starting offsets
      	int fieldOffset = -1;
	   	int methodOffset = 0;
      	
	   	// EXTENDS
      	if ( ctx.EXTENDS( ) != null ) {
      		// superclass name
      		String suID = ctx.suID.getText( );

      		// superclass not defined
      		if ( classTable.getClassVT( suID ) == null ) {
				System.out.println( "Class ID '" + suID + "' not found at line " + ctx.suID.getLine( ) );
	    		stErrors++;
			}

      		// set class' parent as the superclass' STentry (from symbol table)
			clsNode.setSuper( stFront.get( suID ) );
			// get superclass' type
			superClassType = ( ClassTypeNode ) stFront.get( suID ).getRetType( );

			// reset fields and methods starting offsets according to superclass definitions
	        fieldOffset = -1 - superClassType.getFields( ).size( );
	        methodOffset = superClassType.getMethods( ).size( );
	        
	        // copy each superclass' definition into (this) class table 
	        classTable.getClassVT( suID ).forEach( (k, v) -> stClsNestedLevel.put( k, v ) );
	        // declare supertyping
	        lib.setSuperType( clsID, suID ); 
      	}
      	
	   	// data structure for redefinition-check optimization
	   	Set<String> definedElements = new HashSet<>( );
      	
      	// FIELDS
      	for ( int i = 0; ctx.field( ) != null && i < ctx.field( ).size( ); i++ ) {
      		// get FieldNode visiting his declaration
      		FieldNode fieldNode = ( FieldNode ) visit( ctx.field( i ) );
      		
      		// add field to class
      		clsNode.addField( fieldNode );

      		// check for field redefinition (in this class)
      		if( definedElements.stream( ).anyMatch( e -> e.equals( fieldNode.getID( ) ) ) ) {
      			System.out.println( "Redefinition of field " + fieldNode.getID( ) + " at line " + ctx.field( i ).fID.getLine( ) );
      			stErrors++;
      		} else definedElements.add( fieldNode.getID( ) );
      		
      		// try to get previous field declaration
			STEntry val = stClsNestedLevel.get( fieldNode.getID( ) );

			// a previous declared field exist
			if( val != null ) {

				// superclass exist and has this parameter -> ok, override
				if ( superClassType != null && superClassType.getFields( ).stream( ).map( e -> ( FieldNode ) e ).anyMatch( e -> e.getID( ).equals( fieldNode.getID( ) ) ) ) {

					// substitute field in (this) class table
					stClsNestedLevel.put( fieldNode.getID( ), new STEntry( symTable.getLevel( ), fieldNode.getSymType( ), val.getOffset( ), false ) );
					fieldNode.setOffset( val.getOffset( ) );
					
				// superclass does not contains this field -> the duplicate declaration is made in this class
				} else {
					System.out.println( "Field '" + fieldNode.getID( ) + "' at line " + ctx.field( i ).fID.getLine( ) + " already defined in class '" + clsID + "'" );
		    		stErrors++;
				}
			
			// no field with this name exist -> no overriding
			} else {

				// add field in class table
				stClsNestedLevel.put( fieldNode.getID( ), new STEntry( symTable.getLevel( ), fieldNode.getSymType( ), fieldOffset, false ) );
				fieldNode.setOffset( fieldOffset-- );
			}
      	}
      	
      	// METHODS
      	for ( int i = 0; ctx.method( ) != null && i < ctx.method( ).size( ); i++ ) {
      		// get MethodNode visiting his declaration
      		MethodNode methodNode = ( MethodNode ) visit( ctx.method( i ) );

      		// add method to class
      		clsNode.addMethod( methodNode );
      		
      		// check for method redefinition (in this class)
      		if( definedElements.stream( ).anyMatch( e -> e.equals( methodNode.getID( ) ) ) ) {
      			System.out.println( "Redefinition of field " + methodNode.getID( ) + " at line " + ctx.method( i ).mID.getLine( ) );
      			stErrors++;
      		} else definedElements.add( methodNode.getID( ) );

      		// try to get previous method declaration
      		STEntry val = stClsNestedLevel.get( methodNode.getID( ) );

      		// a previous declared method exist
			if( val != null ){

				// superclass exist and has this method -> ok, override
				if ( superClassType != null && classTable.getClassVT( ctx.suID.getText( ) ).get( methodNode.getID( ) ) != null ) {

					// substitute method in (this) class table
					stClsNestedLevel.put( methodNode.getID( ), new STEntry( symTable.getLevel( ), new ArrowTypeNode( methodNode.getParameters( ), methodNode.getSymType( ) ), val.getOffset( ), true ) );
					methodNode.setOffset( val.getOffset( ) );
					
				// superclass does not contains this method -> the duplicate declaration is made in this class
				} else {
					System.out.println( "Method '" + methodNode.getID( ) + "' at line " + ctx.method( i ).mID.getLine( ) + " already defined in class '" + clsID + "'" );
		    		stErrors++;
				}

			// no method with this name exist -> no overriding
			} else {

				// add method in class table
				stClsNestedLevel.put( methodNode.getID( ), new STEntry( symTable.getLevel( ), new ArrowTypeNode( methodNode.getParameters( ), methodNode.getSymType( ) ), methodOffset, true ) );
				methodNode.setOffset( methodOffset++ );
			}
      	}

      	// get (this) class declarations' table and add it to classTable
      	Map<String, STEntry> virtualTable = symTable.popTable( );
      	classTable.addClassVT( clsID, virtualTable );

      	// get (offset-sorted) methods and add them to ClassTypeNode
      	virtualTable.values( ).stream( ).filter( STEntry::isMethod )
      			.sorted( ( e1, e2 ) -> e1.getOffset( ) - e2.getOffset( ) ).forEach( e -> clsTypeNode.addMethod( e.getRetType( ) ) );

      	// get (offset-sorted) fields and add them to ClassTypeNode
		virtualTable.entrySet( ).stream( ).filter( e -> ! e.getValue( ).isMethod( ) )
				.sorted( ( e1, e2 ) -> e2.getValue( ).getOffset( ) - e1.getValue( ).getOffset( ) )
				.forEach( e -> clsTypeNode.addField( new FieldNode( e.getKey( ), e.getValue( ).getRetType( ), e.getValue( ).getOffset( ) ) ) );

		return clsNode;
	}

	@Override
	public Node visitDec(FOOLParser.DecContext ctx) {
		Map<String,STEntry> stFront = symTable.getTable( );
		
		if ( ctx.VAR( ) != null ) {
			VarNode varNode = new VarNode( ctx.vID.getText( ), visit( ctx.vT ), visit( ctx.vE ) );  
	   
			// functional-type variables take double space (function addr. & Frame Pointer, i.e. addr. of this Activation Record) -> decrement offset two time
			if ( varNode.getSymType( ) instanceof ArrowTypeNode )
				offset--;

			if ( stFront.put( varNode.getID( ), new STEntry( symTable.getLevel( ), varNode.getSymType( ), offset-- ) ) != null ) {
				System.out.println( "Var ID '" + varNode.getID( ) + "' at line " + ctx.vID.getLine( ) + " already declared" );
				stErrors++;
			}
			
			return varNode;
		} else {
			FunNode funNode = new FunNode( ctx.fID.getText( ), visit( ctx.fT ) );
			
			List<Node> parTypes = new ArrayList<Node>( );

			if ( stFront.put( funNode.getID( ), new STEntry( symTable.getLevel( ), new ArrowTypeNode( parTypes, funNode.getSymType( ) ), offset-- ) ) != null ) {
                System.out.println( "Fun ID '" + funNode.getID( ) + "' at line " + ctx.fID.getLine( ) + " already declared" );
                stErrors++;
			}

            offset--;  // perche e' funzionale
            
            Map<String,STEntry> funNestingLevel = symTable.nestTable( );
            
            int parOffset = 1;
            
            // PARAMETERS
            for ( int i = 0; i < ctx.parameter( ).size( ); i++ ) {
            	ParNode parNode = ( ParNode ) visit( ctx.parameter( i ) );
            	parTypes.add( parNode.getSymType( ) );
            	
            	if ( parNode.getSymType( ) instanceof ArrowTypeNode )  // Se di tipo funzionale
            		parOffset++;
            	
            	funNode.addParameter( parNode );
            	
            	if ( funNestingLevel.put( parNode.getID( ), new STEntry( symTable.getLevel( ), parNode.getSymType( ), parOffset++ ) ) != null ) { //aggiungo dich a hmn
            		System.out.println( "Parameter ID '" + parNode.getID( ) + "' at line " + ctx.parameter( i ).pID.getLine( ) + " already declared" );
            		stErrors++;
            	}
			}
            
            int oldOffset = offset;
	    	offset = -2;
	    	
	    	for ( int i = 0; ctx.dec( ) != null && i < ctx.dec( ).size( ); i++ ) {
	    		funNode.addDeclaration( visit( ctx.dec( i ) ) );
	    	}
	    	
	    	// reset offset to old value
	    	offset = oldOffset;
	    	
	    	// add method expression
	    	funNode.setExpession( visit( ctx.fE ) );

	      	// remove method's symbol table (exiting the method scope)              
	       	symTable.popTable( );
			
			return funNode;
		}
	}

	@Override
	public Node visitField(FOOLParser.FieldContext ctx) {
		return new FieldNode( ctx.fID.getText( ), visit( ctx.fT ), 0 );
	}
	
	@Override
	public Node visitMethod(FOOLParser.MethodContext ctx) {
		// create method node
		MethodNode methodNode = new MethodNode( ctx.mID.getText( ), visit( ctx.mT ) );

		// create new "nested" table in symTable (method scope)
		Map<String,STEntry> mthdNestingLevel = symTable.nestTable( );
		
		// parameters start offset
		int parOffset = 1;

		for ( int i = 0; ctx.parameter( ) != null && i < ctx.parameter( ).size( ); i++ ) {
			// parse parameter
			ParNode par = ( ParNode ) visit( ctx.parameter( i ) );
			
			// add parameter to method
          	methodNode.addParameter( par );
          	
          	// check parameter existence in method's symbol table
          	if ( mthdNestingLevel.put( par.getID( ), new STEntry( symTable.getLevel( ), par.getSymType( ), parOffset++, false ) ) != null  ) {
           		System.out.println( "Parameter ID '" + par.getID( ) + "' at line " + ctx.parameter( i ).pID.getLine( ) + " already declared" );
        		stErrors++;
        	}
      	}

		// set offset (coming into a new nested scope) saving the old value
    	int oldOffset = offset;
    	offset = -2;
    	
    	for ( int i = 0; ctx.var( ) != null && i < ctx.var( ).size( ); i++ ) {
    		methodNode.addDeclaration( visit( ctx.var( i ) ) );
    	}
    	
    	// reset offset to old value
    	offset = oldOffset;

    	// add method expression
    	methodNode.setExpession( visit( ctx.mE ) );

      	// remove method's symbol table (exiting the method scope)              
       	symTable.popTable( );
		
		return methodNode;
	}
	
	@Override
	public Node visitParameter(FOOLParser.ParameterContext ctx) {
		// create new parameter node
     	return new ParNode( ctx.pID.getText( ), visit( ctx.pT ) );	
	}

	@Override
	public Node visitVar(FOOLParser.VarContext ctx) {
		VarNode varNode = new VarNode( ctx.vID.getText( ), visit( ctx.vT ), visit( ctx.vE ) ); 
		
		Map<String,STEntry> stFront = symTable.getTable( );
		   
		// functional-type variables take double space (function addr. & Frame Pointer, i.e. addr. of this Activation Record) -> decrement offset two time
		if ( varNode.getSymType( ) instanceof ArrowTypeNode )
			offset--;

		if ( stFront.put( varNode.getID( ), new STEntry( symTable.getLevel( ), varNode.getSymType( ), offset-- ) ) != null ) {
			System.out.println( "Var ID '" + varNode.getID( ) + "' at line " + ctx.vID.getLine( ) + " already declared" );
			stErrors++;
		}
		
		return varNode;
	}
	
	@Override
	public Node visitHotype(FOOLParser.HotypeContext ctx) {
		return visit( ctx.type( ) == null ? ctx.arrow( ) : ctx.type( ) );
	}
	
	@Override
	public Node visitIntType(FOOLParser.IntTypeContext ctx) {
		return new IntTypeNode( );
	}
	
	@Override
	public Node visitBoolType(FOOLParser.BoolTypeContext ctx) {
		return new BoolTypeNode( );
	}
	
	@Override
	public Node visitIdType(FOOLParser.IdTypeContext ctx) {
		return new RefTypeNode( ctx.ID( ).getText( ) );
	}

	@Override
	public Node visitArrow(FOOLParser.ArrowContext ctx) {
		List<Node> parameters = new ArrayList<>( );
		
		for ( int i = 0; i < ctx.hotype( ).size( ); i++ )
			parameters.add( visit( ctx.hotype( i ) ) );
		
		return new ArrowTypeNode( parameters, visit( ctx.type( ) ) );
	}
	
	@Override
	public Node visitExp(FOOLParser.ExpContext ctx) {
		if ( ! ctx.PLUS( ).isEmpty( ) ) return new PlusNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.MINUS( ).isEmpty( ) ) return new MinusNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.OR( ).isEmpty( ) ) return new OrNode( visit( ctx.l ), visit( ctx.r ) );
		return visit( ctx.l );
	}
	
	@Override
	public Node visitTerm(FOOLParser.TermContext ctx) {		
		if ( ! ctx.TIMES( ).isEmpty( ) ) return new TimesNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.DIV( ).isEmpty( ) ) return new DivNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.AND( ).isEmpty( ) ) return new AndNode( visit( ctx.l ), visit( ctx.r ) );
		return visit( ctx.l );
	}
	
	@Override
	public Node visitFactor(FOOLParser.FactorContext ctx) {
		if ( ! ctx.EQ( ).isEmpty( ) ) return new EqualNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.GE( ).isEmpty( ) ) return new GreaterEqualNode( visit( ctx.l ), visit( ctx.r ) );
		if ( ! ctx.LE( ).isEmpty( ) ) return new LessEqualNode( visit( ctx.l ), visit( ctx.r ) );
		return visit( ctx.l );
	}
	
	@Override
	public Node visitIntegerValue(FOOLParser.IntegerValueContext ctx) {
		return new IntNode( Integer.parseInt( ctx.INTEGER( ).getText( ) ) );
	}
	
	@Override
	public Node visitBooleanValue(FOOLParser.BooleanValueContext ctx) {
		return new BoolNode( ctx.TRUE( ) != null );
	}
	
	@Override
	public Node visitNullValue(FOOLParser.NullValueContext ctx) {
		return new EmptyNode( );
	}
	
	@Override
	public Node visitNewValue(FOOLParser.NewValueContext ctx) {
		Map<String, STEntry> virtualTable = classTable.getClassVT( ctx.ID( ).getText( ) );

		if ( virtualTable == null ) {
			System.out.println( "Class ID '" + ctx.ID( ).getText( ) + "' not found at line " + ctx.ID( ).getSymbol( ).getLine( ) );
    		stErrors++;
		}
		
		STEntry classRef = symTable.getTable( 0 ).get( ctx.ID( ).getText( ) );
		
		if ( classRef == null ) {
			System.out.println( "Class ID '" + ctx.ID( ).getText( ) + "' doesn't exist at line " + ctx.ID( ).getSymbol( ).getLine( ) );
    		stErrors++;
		}
		
		NewNode newNode = new NewNode( ctx.ID( ).getText( ), symTable.getTable( 0 ).get( ctx.ID( ).getText( ) ) );
		
		for ( int i = 0; i < ctx.exp( ).size( ); i++ ) {
			newNode.addField( visit( ctx.exp( i ) ) );
		}

		return newNode;
	}
	
	@Override
	public Node visitIfThenElseValue(FOOLParser.IfThenElseValueContext ctx) {
		return new IfNode( visit( ctx.exp( 0 ) ), visit( ctx.exp( 1 ) ), visit( ctx.exp( 2 ) ) );
	}

	@Override
	public Node visitNotValue(FOOLParser.NotValueContext ctx) {
		return new NotNode( visit( ctx.exp( ) ) );
	}
	
	@Override
	public Node visitPrintValue(FOOLParser.PrintValueContext ctx) {
		return new PrintNode( visit( ctx.exp( ) ) );
	}

	@Override
	public Node visitParenthesisBlockValue(FOOLParser.ParenthesisBlockValueContext ctx) {
		return visit( ctx.exp( ) );
	}

	@Override
	public Node visitIdValue(FOOLParser.IdValueContext ctx) {		
		STEntry entry = null;
		
		for ( int nl = symTable.getLevel( ); nl >= 0 && entry == null; nl-- ) {
			entry = ( symTable.getTable( nl ) ).get( ctx.ID( ).getText( ) );
		}

       	if ( entry == null ) {
         	System.out.println( "ID '" + ctx.ID( ).getText( ) + "' at line " + ctx.ID( ).getSymbol( ).getLine( ) + " not declared" );
         	stErrors++; 
        }

       	return new IdNode( ctx.ID( ).getText( ), entry, symTable.getLevel( ) );
	}

	@Override
	public Node visitFunctionCallValue(FOOLParser.FunctionCallValueContext ctx) {
		STEntry entry = null;
		
		for ( int nl = symTable.getLevel( ); nl >= 0 && entry == null; nl-- ) {
			entry=( symTable.getTable( nl ) ).get( ctx.ID( ).getText( ) );
		}

       	if ( entry == null ) {
         	System.out.println( "ID '" + ctx.ID( ).getText( ) + "' at line " + ctx.ID( ).getSymbol( ).getLine( ) + " not declared" );
         	stErrors++; 
        }
        
       	List<Node> arglist = new ArrayList<Node>( );
       	
       	for ( int i = 0; i < ctx.exp( ).size( ); i++ ) {
			arglist.add( visit( ctx.exp( i ) ) );
		}

       	return new CallNode( ctx.ID( ).getText( ), entry, arglist, symTable.getLevel( ) );
	}

	@Override
	public Node visitMethodCallValue(FOOLParser.MethodCallValueContext ctx) {
		STEntry entry = null;
		
		for ( int nl = symTable.getLevel( ); nl >= 0 && entry == null; nl-- ) {
			entry=( symTable.getTable( nl ) ).get( ctx.oID.getText( ) );
		}

       	if ( entry == null ) {
         	System.out.println( "ID '" + ctx.oID.getText( ) + "' at line " + ctx.oID.getLine( ) + " not declared" );
         	stErrors++; 
        }
       	
		RefTypeNode reference = ( RefTypeNode ) entry.getRetType( );

		STEntry methodEntry = classTable.getClassVT( reference.getID( ) ).get( ctx.mID.getText( ) );		// method entry

		ClassCallNode clsCallNode = new ClassCallNode( ctx.mID.getText( ), entry, methodEntry, symTable.getLevel( ) );

		for ( int i = 0; i < ctx.exp( ).size( ); i++) {
			clsCallNode.addParameter( visit( ctx.exp( i ) ) );
		}

		return clsCallNode;
	}
}
