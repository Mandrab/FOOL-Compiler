import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ast.ArrowTypeNode;
import ast.BoolTypeNode;
import ast.ClassNode;
import ast.ClassTypeNode;
import ast.FieldNode;
import ast.FunNode;
import ast.IntNode;
import ast.IntTypeNode;
import ast.MethodNode;
import ast.Node;
import ast.ParNode;
import ast.ProgLetInNode;
import ast.ProgNode;
import ast.RefTypeNode;
import ast.STentry;
import ast.VarNode;

import static lib.FOOLlib.superType;

public class ParserVisitor extends FOOLBaseVisitor<Node> {
	
	private int stErrors;
	
	private SymbolTable symTable;
	private ClassTable classTable;
	private int offset;
	
	public ParserVisitor( ) {
		symTable = new SymbolTable( );
		classTable = new ClassTable( );
		offset = -2;
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
		ClassNode clsNode = new ClassNode( clsTypeNode );

		// get last symbol table (where put class declaration)
		Map<String, STentry> stFront = symTable.getTable( );
		
		// add class declaration to symTable
		if ( stFront.put( clsID, new STentry( symTable.getLevel( ), clsTypeNode, offset--, false ) ) != null ) {
	        System.out.println( "Class ID '" + clsID + "' at line " + ctx.clsID.getLine( ) + " already declared" );
	    	stErrors++;
	    }

		// create a nested table for class' declarations
      	Map<String,STentry> stClsNestedLevel = symTable.nestTable( );
      	
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
			superClassType = ( ClassTypeNode ) stFront.get( suID ).getType( );
			
			// reset fields and methods starting offsets according to superclass definitions
	        fieldOffset = -1 - superClassType.getAllFields( ).size( );
	        methodOffset = superClassType.getAllMethods( ).size( );
	        
	        // copy each superclass' definition into (this) class table 
	        classTable.getClassVT( suID ).forEach( (k, v) -> stClsNestedLevel.put( k, v ) );
	        // declare supertyping
	        superType.put( clsID, suID ); 
      	}
      	
      	// FIELDS
      	for ( int i = 0; ctx.field( ) != null && i < ctx.field( ).size( ); i++ ) {
      		// get FieldNode visiting his declaration
      		FieldNode fieldNode = ( FieldNode ) visit( ctx.field( i ) );
      		
      		// add field to class
      		clsNode.setField( fieldNode );
      		
      		// try to get previous field declaration
			STentry val = stClsNestedLevel.get( fieldNode.getID( ) );

			// a previous declared field exist
			if( val != null ) {

				// superclass exist and has this parameter -> ok, override
				if ( superClassType != null && superClassType.getAllFields( ).stream( ).map( e -> ( FieldNode ) e ).anyMatch( e -> e.getID( ).equals( fieldNode.getID( ) ) ) ) {

					// substitute field in (this) class table
					stClsNestedLevel.put( fieldNode.getID( ), new STentry( symTable.getLevel( ), fieldNode.getType( ), val.getOffset( ), false ) );
					fieldNode.setOffset( val.getOffset( ) );
					
				// superclass does not contains this field -> the duplicate declaration is made in this class
				} else {
					System.out.println( "Field '" + fieldNode.getID( ) + "' at line " + ctx.field( i ).fID.getLine( ) + " already defined in class '" + clsID + "'" );
		    		stErrors++;
				}
			
			// no field with this name exist -> no overriding
			} else {

				// add field in class table
				stClsNestedLevel.put( fieldNode.getID( ), new STentry( symTable.getLevel( ), fieldNode.getType( ), fieldOffset, false ) );
				fieldNode.setOffset( fieldOffset-- );
			}
      	}
      	
      	// METHODS
      	for ( int i = 0; ctx.field( ) != null && i < ctx.field( ).size( ); i++ ) {
      		// get MethodNode visiting his declaration
      		MethodNode methodNode = ( MethodNode ) visit( ctx.field( i ) );

      		// add method to class
      		clsNode.setMethod( methodNode );

      		// try to get previous method declaration
      		STentry val = stClsNestedLevel.get( methodNode.getID( ) );

      		// a previous declared method exist
			if( val != null ){
				
				// superclass exist and has this method -> ok, override
				if ( superClassType != null && superClassType.getAllMethods( ).stream( ).map( e -> ( MethodNode ) e ).anyMatch( e -> e.getID( ).equals( methodNode.getID( ) ) ) ) {

					// substitute method in (this) class table
					stClsNestedLevel.put( methodNode.getID( ), new STentry( symTable.getLevel( ), methodNode.getType( ), val.getOffset( ), true ) );
					methodNode.setOffset( val.getOffset( ) );
					
				// superclass does not contains this method -> the duplicate declaration is made in this class
				} else {
					System.out.println( "Method '" + methodNode.getID( ) + "' at line " + ctx.method( i ).mID.getLine( ) + " already defined in class '" + clsID + "'" );
		    		stErrors++;
				}

			// no method with this name exist -> no overriding
			} else {

				// add method in class table
				stClsNestedLevel.put( methodNode.getID( ), new STentry( symTable.getLevel( ), methodNode.getType( ), methodOffset++, true ) );
				methodNode.setOffset( methodOffset++ );
			}
      	}
      	
      	// get (this) class declarations' table and add it to classTable
      	Map<String, STentry> virtualTable = symTable.popTable( );
      	classTable.addClassVT( clsID, virtualTable );
      	
      	// get (offset-sorted) methods and add them to ClassTypeNode
      	virtualTable.entrySet( ).stream( ).filter( e -> e.getValue( ).isMethod( ) ).sorted( ( e1, e2 ) -> e1.getValue( ).getOffset( ) - e2.getValue( ).getOffset( ) ).forEach(e ->
			clsTypeNode.setMethod( new MethodNode( e.getKey( ), e.getValue( ).getType( ) ) ) );
		
      	// get (offset-sorted) fields and add them to ClassTypeNode
		virtualTable.entrySet().stream().filter( e -> !e.getValue().isMethod() ).sorted( (e1, e2) -> e2.getValue().getOffset() - e1.getValue().getOffset() ).forEach(e ->
			clsTypeNode.setField( new FieldNode( e.getKey( ), e.getValue( ).getType( ) ) ) );

		return clsNode;
	}
	
	@Override
	public Node visitDec(FOOLParser.DecContext ctx) {
		Map<String,STentry> stFront = symTable.getTable( );
		
		if ( ctx.VAR( ) != null ) {
			VarNode varNode = new VarNode( ctx.vID.getText( ), visit( ctx.vT ), visit( ctx.vE ) );  
	   
			// functional-type variables take double space (function addr. & Frame Pointer, i.e. addr. of this Activation Record) -> decrement offset two time
			if ( varNode.getSymType( ) instanceof ArrowTypeNode )
				offset--;

			if ( stFront.put( varNode.getID( ), new STentry( symTable.getLevel( ), varNode.getSymType( ), offset-- ) ) != null ) {
				System.out.println( "Var ID '" + varNode.getID( ) + "' at line " + ctx.vID.getLine( ) + " already declared" );
				stErrors++;
			}
			
			return varNode;
		} else {
			FunNode funNode = new FunNode( ctx.fID.getText( ), visit( ctx.fT ) );
			
			List<Node> parTypes = new ArrayList<Node>( );
			
			if ( stFront.put( funNode.getID( ), new STentry( symTable.getLevel( ), new ArrowTypeNode( parTypes, funNode.getSymType( ) ), offset-- ) ) != null ) {
                System.out.println( "Fun ID '" + funNode.getID( ) + "' at line " + ctx.fID.getLine( ) + " already declared" );
                stErrors++;
			}

            offset--;  // perche e' funzionale
            
            Map<String,STentry> funNestingLevel = symTable.nestTable( );
            
            int parOffset = 1;
            
            // PARAMETERS
            for ( int i = 0; i < ctx.parameter( ).size( ); i++ ) {
            	ParNode parNode = ( ParNode ) visit( ctx.parameter( i ) );
            	parTypes.add( parNode.getSymType( ) );
            	
            	if ( parNode.getSymType( ) instanceof ArrowTypeNode )  // Se di tipo funzionale
            		parOffset++;
            	
            	funNode.addPar( parNode );
            	
            	if ( funNestingLevel.put( parNode.getID( ), new STentry( symTable.getLevel( ), parNode.getSymType( ), parOffset++ ) ) != null ) { //aggiungo dich a hmn
            		System.out.println( "Parameter ID '" + parNode.getID( ) + "' at line " + ctx.parameter( i ).pID.getLine( ) + " already declared" );
            		stErrors++;
            	}
			}
            
            int oldOffset = offset;
	    	offset = -2;
	    	
	    	for ( int i = 0; ctx.dec( ) != null && i < ctx.dec( ).size( ); i++ ) {
	    		funNode.addDec( visit( ctx.dec( i ) ) );
	    	}
	    	
	    	// reset offset to old value
	    	offset = oldOffset;
	    	
	    	// add method expression
	    	funNode.addBody( visit( ctx.fE ) );

	      	// remove method's symbol table (exiting the method scope)              
	       	symTable.popTable( );
			
			return funNode;
		}
	}
	
	@Override
	public Node visitField(FOOLParser.FieldContext ctx) {
		return new FieldNode( ctx.fID.getText( ), visit( ctx.fT ) );
	}
	
	@Override
	public Node visitMethod(FOOLParser.MethodContext ctx) {	
		// create method node
		MethodNode methodNode = new MethodNode( ctx.mID.getText( ), visit( ctx.mT ) );

		// create new "nested" table in symTable (method scope)
		Map<String,STentry> mthdNestingLevel = symTable.nestTable( );
		
		// parameters start offset
		int parOffset = 1;

		for ( int i = 0; ctx.parameter( ) != null && i < ctx.parameter( ).size( ); i++ ) {
			// parse parameter
			ParNode par = ( ParNode ) visit( ctx.parameter( i ) );
			
			// add parameter to method
          	methodNode.addPar( par );
          	
          	// check parameter existence in method's symbol table
          	if ( mthdNestingLevel.put( par.getID( ), new STentry( symTable.getLevel( ), par.getSymType( ), parOffset++, false ) ) != null  ) {
           		System.out.println( "Parameter ID '" + par.getID( ) + "' at line " + ctx.parameter( i ).pID.getLine( ) + " already declared" );
        		stErrors++;
        	}
      	}

		// set offset (coming into a new nested scope) saving the old value
    	int oldOffset = offset;
    	offset = -2;
    	
    	for ( int i = 0; ctx.var( ) != null && i < ctx.var( ).size( ); i++ ) {
    		methodNode.addDec( visit( ctx.var( i ) ) );
    	}
    	
    	// reset offset to old value
    	offset = oldOffset;

    	// add method expression
    	methodNode.addBody( visit( ctx.mE ) );

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
	public Node visitNewValue(FOOLParser.NewValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitArrow(FOOLParser.ArrowContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitParenthesisBlockValue(FOOLParser.ParenthesisBlockValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitIdValue(FOOLParser.IdValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitNullValue(FOOLParser.NullValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitNotValue(FOOLParser.NotValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitIfThenElseValue(FOOLParser.IfThenElseValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitFunctionCallValue(FOOLParser.FunctionCallValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitMethodCallValue(FOOLParser.MethodCallValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitTerm(FOOLParser.TermContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitIntegerValue(FOOLParser.IntegerValueContext ctx) {
		return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
	}

	@Override
	public Node visitBooleanValue(FOOLParser.BooleanValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitPrintValue(FOOLParser.PrintValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitExp(FOOLParser.ExpContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitFactor(FOOLParser.FactorContext ctx) {
		return visitChildren(ctx);
	}

}
