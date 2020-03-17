import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ast.ClassNode;
import ast.ClassTypeNode;
import ast.IntNode;
import ast.Node;
import ast.ProgLetInNode;
import ast.ProgNode;
import ast.STentry;

public class FoolVisitorImpl extends FOOLBaseVisitor<Node> {
	
	private SymbolTable symTable;
	private ClassTable classTable;
	private int stErrors;
	
	public FoolVisitorImpl( ) {
		symTable = new SymbolTable( );
		classTable = new ClassTable( );
	}
	
	@Override
	public Node visitProg(FOOLParser.ProgContext ctx) {
		symTable.nestTable( );

		List<Node> declarations = new ArrayList<Node>( );
		
		for ( int i = 0; ctx.cllist( ) != null && i < ctx.cllist( ).getChildCount( ); i++ )
			declarations.add( visit( ctx.cllist( ).getChild( i ) ) );

		for ( int i = 0; ctx.declist( ) != null && i < ctx.declist( ).getChildCount( ); i++ )
			declarations.add( visit( ctx.declist( ).getChild( i ) ) );

		Node exp = visit( ctx.exp( ) );
		
		symTable.popTable( );
		
		return ctx.LET( ) != null ? new ProgLetInNode( declarations,  exp ) : new ProgNode( exp );
	}
	
	@Override
	public Node visitCls(FOOLParser.ClsContext ctx) {
		Map<String, STentry> stFront = symTable.getTable( );
		int clsOffset = -2 - stFront.size( );
	   	
	   	ClassTypeNode clsTypeNode = new ClassTypeNode( );
		ClassNode clsNode = new ClassNode( clsTypeNode );
		
		if ( stFront.put( ctx.ID( 0 ).getText( ), new STentry( symTable.getLevel( ), new ClassTypeNode( ), clsOffset, false ) ) != null ) {
	        System.out.println( "Class id " + ctx.ID( 0 ).getText( ) + " at line " + ctx.getStart( ).getLine( ) + " already declared" );
	    	stErrors++;
	    }

      	Map<String,STentry> stClsNestedLevel = symTable.nestTable( );
      	
      	if ( ctx.EXTENDS( ) != null ) {
      		String suID = ctx.ID( 1 ).getText( );

      		if ( classTable.getClassVT( suID ) == null ) {
				System.out.println( "Class id " + suID + " not found at line " + ctx.getStart( ).getLine( ) );
	    		stErrors++;
			}

			clsNode.setSuper( stFront.get( suID ) );
			ClassTypeNode superClassType = ( ClassTypeNode ) stFront.get( suID ).getType( );
			
	        int fieldOffset = -1 - superClassType.getAllFields( ).size( );
	        int methodOffset = superClassType.getAllMethods( ).size( );
	        
	        classTable.getClassVT( suID ).forEach( (k, v) -> stClsNestedLevel.put( k, v ) );            // add super's elements ( fields and methods ) to this class' table
      	}
      	
		
		return visitChildren(ctx);
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
	public Node visitIntType(FOOLParser.IntTypeContext ctx) {
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
	public Node visitHotype(FOOLParser.HotypeContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitNullValue(FOOLParser.NullValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitDeclist(FOOLParser.DeclistContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitNotValue(FOOLParser.NotValueContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Node visitBoolType(FOOLParser.BoolTypeContext ctx) {
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

	@Override
	public Node visitIdType(FOOLParser.IdTypeContext ctx) {
		return visitChildren(ctx);
	}

}
