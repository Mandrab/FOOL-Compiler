package visitors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import ast.AndNode;
import ast.ArrowTypeNode;
import ast.BoolNode;
import ast.BoolTypeNode;
import ast.CallNode;
import ast.ClassCallNode;
import ast.ClassNode;
import ast.ClassTypeNode;
import ast.DecNode;
import ast.DivNode;
import ast.EmptyNode;
import ast.EmptyTypeNode;
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
import lib.FOOLLib;
import lib.TypeException;

// TODO commento
//fa il type checking e ritorna: 
	//per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
	//per una dichiarazione, "null"
public class TypeCheckerVisitor extends ReflectionVisitor<Node> implements NodeVisitor<Node> {

	private final FOOLLib lib;
	
	public TypeCheckerVisitor( FOOLLib globalLib ) {
		lib = globalLib;
	}
	
	@Override
	public Node visit( Node element ) {
		try {
			return super.visit( element );
		} catch ( InvocationTargetException exception ) {
			Throwable throwable = exception;
			while( throwable.getCause( ) != null )
				throwable = exception.getCause( );

			if ( throwable instanceof TypeException ) throw ( RuntimeException ) throwable;
			exception.printStackTrace( );
		}

		return null;
	}
	
	@Override
	public Node visit( AndNode element ) {
		Node leftType = visit( element.getLeft( ) );  
		Node rightType = visit( element.getRight( ) );  
		if ( ! ( leftType instanceof BoolTypeNode && rightType instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in and", lib );
		return new BoolTypeNode();
	}

	@Override
	public Node visit( ArrowTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( BoolNode element ) {
		return new BoolTypeNode( );
	}

	@Override
	public Node visit( BoolTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( CallNode element ) {
		if ( ! ( element.getType( ) instanceof ArrowTypeNode ) )
			throw TypeException.buildAndMark( "Invocation of a non-function " + element.getID( ), lib );
		ArrowTypeNode callStructure = ( ArrowTypeNode ) element.getType( );
		List<Node> callParameters = callStructure.getParameters( );
		if ( ! ( callParameters.size( ) == element.getParameters( ).size( ) ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in the invocation of " + element.getID( ), lib );
		for ( int i = 0; i < element.getParameters( ).size( ); i++ )
			if ( ! ( lib.isSubtype( visit( element.getParameters( ).get( i ) ), callParameters.get( i ) ) ) )
				throw TypeException.buildAndMark( "Wrong type for " + (i + 1) + "-th parameter in the invocation of " + element.getID( ), lib );
		return callStructure.getRetType( );
	}

	@Override
	public Node visit( ClassCallNode element ) {
		if ( ! ( element.getMethodEntry( ).getRetType( ) instanceof ArrowTypeNode ) )
			throw TypeException.buildAndMark( "Invocation of a non-method " + element.getID( ), lib );

		ArrowTypeNode arrowNode = ( ArrowTypeNode ) element.getMethodEntry( ).getRetType( );
		List<Node> parameters = arrowNode.getParameters( );

		if ( ! ( parameters.size( ) == element.getParameters( ).size( ) ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in the invocation of method " + element.getID( ), lib );
		
		for ( int i = 0, count = 0; i < element.getParameters( ).size( ); i++, count++ ) {
			Node parameter = element.getParameters( ).get( i );

			if ( parameter instanceof IdNode )
				parameter = ( ( IdNode ) parameter).getEntry( ).getRetType( );
			else if ( parameter instanceof DecNode )
				parameter = ( ( DecNode ) parameter ).getSymType( );
			else if ( parameter instanceof CallNode )
				parameter = ( ( CallNode ) parameter ).getType( );
			else if ( parameter instanceof ClassCallNode )
				parameter = ( ( ClassCallNode ) parameter ).getRetType( );
			else parameter = visit( parameter );

			if ( parameter instanceof ArrowTypeNode )
				parameter = ( ( ArrowTypeNode ) parameter ).getRetType( );
			
			if ( ! ( lib.isSubtype( parameter, ( ( ParNode ) parameters.get( count ) ).getSymType( ) ) ) )
				throw TypeException.buildAndMark( "Wrong type of " + ( i + 1 ) + "-th parameter in method " + element.getID( ) + " call", lib );
		}
		
		return arrowNode.getRetType( );
	}

	@Override
	public Node visit( ClassNode element ) {
		for ( Node method : element.getMethods( ) ) visit( method );
		
		if( element.getSuper( ) != null ) { // check overriding
			ClassTypeNode superCTN = ( ClassTypeNode ) element.getSuper( ).getRetType( );
			ClassTypeNode thisCTN = ( ClassTypeNode ) element.getSymType( );

			for ( Node f : element.getFields( ) ) {
				int fieldOffset = -1 -( ( FieldNode ) f ).getOffset( );

				if ( fieldOffset < superCTN.getFields( ).size( ) ) { // override
					FieldNode superField = ( FieldNode ) superCTN.getFields( ).get( fieldOffset );
					FieldNode myField = ( FieldNode ) thisCTN.getFields( ).get( fieldOffset );

					if ( ! lib.isSubtype( myField, superField ) )
						throw TypeException.buildAndMark( "Overriding of field '" + superField.getID( ) + "' has wrong type. Expected: " + superField.getSymType( ) + " (or super). Given: " + myField.getSymType( ), lib );
				}
			}

			for ( Node m : element.getMethods( ) ) {
				MethodNode method = ( MethodNode ) m;

				if ( method.getOffset( ) < superCTN.getMethods( ).size( ) ) { // override
					ArrowTypeNode superMethod = ( ArrowTypeNode ) superCTN.getMethods( ).get( method.getOffset( ) );
					ArrowTypeNode myMethod = ( ArrowTypeNode ) thisCTN.getMethods( ).get( method.getOffset( ) );

					if ( ! lib.isSubtype( myMethod, superMethod ) )
						throw TypeException.buildAndMark( "Overriding of method '" + method.getID( ) + "' has wrong type", lib );
				}
			}
		}

		return null;
	}

	@Override
	public Node visit( ClassTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( DivNode element ) {
		if ( ! lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) ) 
			throw TypeException.buildAndMark( "First element in division is not an integer", lib );
		if ( ! lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) ) 
			throw TypeException.buildAndMark( "Second element in division is not an integer", lib );
		return new IntTypeNode( );
	}

	@Override
	public Node visit( EmptyNode element ) {
		return new EmptyTypeNode( ); 
	}

	@Override
	public Node visit( EmptyTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( EqualNode element ) {
		Node left = visit( element.getLeft( ) );
		Node right = visit( element.getRight( ) );

		if ( left instanceof ArrowTypeNode || right instanceof ArrowTypeNode )
			throw TypeException.buildAndMark( "Incompatible types in equal", lib );

		if ( ! ( lib.isSubtype( left, right ) || lib.isSubtype( right, left ) ) )
			throw TypeException.buildAndMark( "Incompatible types in equal", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( FieldNode element ) {
		return null;
	}

	@Override
	public Node visit( FunNode element ) {
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );

		if ( ! lib.isSubtype( visit( element.getExpession( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Return-type mismatch in function " + element.getID( ), lib );
		return null;
	}

	@Override
	public Node visit( GreaterEqualNode element ) {
		Node left = visit( element.getLeft( ) );
		Node right = visit( element.getRight( ) );

		if ( ! ( lib.isSubtype( left, right ) || lib.isSubtype( right, left ) ) )
			throw TypeException.buildAndMark( "Incompatible types in Greater/Equal comparison", lib );
		return new BoolTypeNode( );
	}

	@Override
	public Node visit( IdNode element ) {
		if ( element.getEntry( ).getRetType( ) instanceof ClassTypeNode )
			throw TypeException.buildAndMark( "Object's ID '" + element.getID( ) + "' cannot be a class name", lib );// TODO object?
		if ( element.getEntry( ).isMethod( ) )
			throw TypeException.buildAndMark( "Object's ID '" + element.getID( ) + "' cannot be a method name", lib );// TODO object?
		return element.getEntry( ).getRetType( );
	}

	@Override
	public Node visit( IfNode element ) {
		if ( ! ( lib.isSubtype( visit( element.getCondition( ) ), new BoolTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "Condition in IF is not a boolean", lib );

		Node thenBranch = visit( element.getThenBranch( ) );
		Node elseBranch = visit( element.getElseBranch( ) );
		if ( lib.isSubtype( thenBranch, elseBranch ) ) return elseBranch;
		if ( lib.isSubtype( elseBranch, thenBranch ) ) return thenBranch;
		System.out.println( "ciao" );
System.out.println( thenBranch.getClass( ) + " " + elseBranch.getClass( ) );System.out.println( "ciao2" );
		Node type = lib.lowestCommonAncestor( thenBranch, elseBranch );
		if ( type == null )
			throw TypeException.buildAndMark( "Incompatible types in then-else branches", lib );

		return type;
	}

	@Override
	public Node visit( IntNode element ) {
		return new IntTypeNode( );
	}

	@Override
	public Node visit( IntTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( LessEqualNode element ) {
		Node left = visit( element.getLeft( ) );
		Node right = visit( element.getRight( ) );

		if ( ! ( lib.isSubtype( left, right ) || lib.isSubtype( right, left ) ) )
			throw TypeException.buildAndMark( "Incompatible types in Lesser/Equal comparison", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( MethodNode element ) {
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );
		
		if ( ! lib.isSubtype( visit( element.getExpession( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Return-type mismatch in method " + element.getID( ), lib );
		return null;
	}

	@Override
	public Node visit( MinusNode element ) {
		if ( ! ( lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "First element in subtraction is not an integer", lib );
		if ( ! ( lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "First element in subtraction is not an integer", lib );

		return new IntTypeNode( );
	}

	@Override
	public Node visit( NewNode element ) {
		if ( ! ( element.getEntry( ).getRetType( ) instanceof ClassTypeNode ) )
			throw TypeException.buildAndMark( "Instantiation of a non-class: " + element.getID( ), lib );

		RefTypeNode refTypeNode = new RefTypeNode( element.getID( ) );

		ClassTypeNode classType = ( ClassTypeNode ) element.getEntry( ).getRetType( );
		List<Node> requiredFields = classType.getFields( );

		if ( requiredFields.size( ) != element.getFields( ).size( ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in " + element.getID( ) + " instantiation. " + requiredFields.size( ) + " required, " + element.getFields( ).size( ) + " given", lib );
		
		
		for ( int i = 0; i < element.getFields( ).size( ); i++ ) {
			Node field = element.getFields( ).get( i );
			FieldNode requiredField = ( FieldNode ) requiredFields.get( i );

			if ( field instanceof IdNode )
				field = ( ( IdNode ) field ).getEntry( ).getRetType( );
			else if ( field instanceof DecNode )
				field = ( ( DecNode ) field ).getSymType( );
			else if ( field instanceof CallNode )
				field = ( ( CallNode ) field ).getType( );
			else if ( field instanceof ClassCallNode )
				field = ( ( ClassCallNode ) field ).getRetType( );
			else field = visit( field );

			if ( field instanceof ArrowTypeNode )
				field = ( ( ArrowTypeNode ) field ).getRetType( );

			if ( ! ( lib.isSubtype( field, requiredField.getSymType( ) ) ) )
				throw TypeException.buildAndMark( "Passed value for " + ( i + 1 ) + "-th parameter is not of type " + requiredField.getSymType( ), lib );
		}

		return refTypeNode;
	}

	@Override
	public Node visit( NotNode element ) {
		Node result = visit( element.getExpression( ) );
		if ( ! ( result instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Non-boolean type in NOT operation", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( OrNode element ) {
		Node left = visit( element.getLeft( ) );
		Node right = visit( element.getRight( ) );

		if ( ! ( left instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "First element in OR is not a boolean", lib );
		if ( ! ( right instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Second element in OR is not a boolean", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( ParNode element ) {
		return null;
	}

	@Override
	public Node visit( PlusNode element ) {
		if ( ! lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "First element in sum is not an integer", lib );
		if ( ! lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "Second element in sum is not an integer", lib );

		return new IntTypeNode( );
	}

	@Override
	public Node visit( PrintNode element ) {
		return visit( element.getExpression( ) );
	}

	@Override
	public Node visit( ProgLetInNode element ) {
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );

		return visit( element.getExpression( ) );
	}

	@Override
	public Node visit( ProgNode element ) {
		return visit( element.getExpression( ) );
	}

	@Override
	public Node visit( RefTypeNode element ) {
		return null;
	}

	@Override
	public Node visit( STEntry element ) {
		return null;
	}

	@Override
	public Node visit( TimesNode element ) {
		if ( ! lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "First element in multiplication is not an integer", lib );
		if ( ! lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "Second element in multiplication is not an integer", lib );

		return new IntTypeNode( );
	}

	@Override
	public Node visit( VarNode element ) {
		if ( ! lib.isSubtype( visit( element.getExpression( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Incompatible value for variable " + element.getID( ), lib );
		return null;
	}
}
