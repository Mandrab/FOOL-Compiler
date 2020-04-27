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

/**
 * AST visitor to type-check program
 * 
 * @author Paolo Baldini
 */
public class TypeCheckerVisitor extends ReflectionVisitor<Node> implements NodeVisitor<Node> {

	private final FOOLLib lib;						// store info used by other visitors

	public TypeCheckerVisitor( FOOLLib globalLib ) {
		lib = globalLib;
	}

	/**
	 * Type-check generic node. Use a trick to throw type-exception without modify
	 * visitor's interface
	 */
	@Override public Node visit( Node element ) {
		// catch type-exceptions (wrapped by runtime exception)
		try {
			return super.visit( element );
		} catch ( InvocationTargetException exception ) {
			// find root exception (presumably, a type-exception)
			Throwable throwable = exception;
			while( throwable.getCause( ) != null )
				throwable = exception.getCause( );

			// if it's a type-exception, throw it again
			if ( throwable instanceof TypeException ) throw ( RuntimeException ) throwable;
			exception.printStackTrace( );
		}

		return null;
	}

	@Override
	public Node visit( AndNode element ) {
		Node leftType = visit( element.getLeft( ) );	// type-check left
		Node rightType = visit( element.getRight( ) );	// type-check right

		// if expression's operands are not boolean, then throw an exception
		if ( ! ( leftType instanceof BoolTypeNode && rightType instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in and", lib );
		return new BoolTypeNode( );
	}

	@Override
	public Node visit( ArrowTypeNode element ) { return null; }

	@Override
	public Node visit( BoolNode element ) {
		return new BoolTypeNode( );
	}

	@Override
	public Node visit( BoolTypeNode element ) { return null; }

	@Override
	public Node visit( CallNode element ) {
		// type of call has to be functional, otherwise throw a type-exception
		if ( ! ( element.getType( ) instanceof ArrowTypeNode ) )
			throw TypeException.buildAndMark( "Invocation of a non-function " + element.getID( ), lib );

		ArrowTypeNode callStructure = ( ArrowTypeNode ) element.getType( );
		List<Node> parameters = callStructure.getParameters( );

		// I want the right number of parameters, otherwise throw a type-exception
		if ( parameters.size( ) != element.getParameters( ).size( ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in the invocation of " + element.getID( ), lib );

		// type-check every passed parameter and control that's sub-type of expected one
		for ( int i = 0; i < element.getParameters( ).size( ); i++ ) {
			Node parameter = element.getParameters( ).get( i );
			Node expectedParameterType = parameters.get( i );

			// visit parameter (get it's 'real' type)
			Node parameterType = visit( parameter );

			// if passed parameter is a functional one but i expect a value, then get it's return type (i need to pass result of function/method)
			if ( parameterType instanceof ArrowTypeNode && ! ( expectedParameterType instanceof ArrowTypeNode ) )
				if ( parameter instanceof CallNode || parameter instanceof ClassCallNode )
					parameterType = ( ( ArrowTypeNode ) parameterType ).getRetType( );
				else throw TypeException.buildAndMark( "Passed value for " + ( i + 1 ) + "-th field is not of type " + expectedParameterType, lib );

			// check sub-typing of parameter
			if ( ! ( lib.isSubtype( parameterType, expectedParameterType ) ) )
				throw TypeException.buildAndMark( "Wrong type of " + ( i + 1 ) + "-th parameter in method '" + element.getID( ) + "' call", lib );
		}

		return callStructure.getRetType( );
	}

	@Override
	public Node visit( ClassCallNode element ) {
		// type of class-call has to be functional, otherwise throw a type-exception
		if ( ! ( element.getMethodEntry( ).getRetType( ) instanceof ArrowTypeNode ) )
			throw TypeException.buildAndMark( "Invocation of a non-method " + element.getID( ), lib );

		ArrowTypeNode arrowNode = ( ArrowTypeNode ) element.getMethodEntry( ).getRetType( );
		List<Node> parameters = arrowNode.getParameters( );

		// I want the right number of parameters, otherwise throw a type-exception
		if ( parameters.size( ) != element.getParameters( ).size( ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in the invocation of method " + element.getID( ), lib );

		// type-check every passed parameter and control that's sub-type of expected one
		for ( int i = 0; i < element.getParameters( ).size( ); i++ ) {
			Node parameter = element.getParameters( ).get( i );
			Node expectedParameterType = ( ( ParNode ) parameters.get( i ) ).getSymType( );

			// visit parameter (get it's 'real' type)
			Node parameterType = visit( parameter );

			// if passed parameter is a functional one but i expect a value, then get it's return type (i need to pass result of function/method)
			if ( parameterType instanceof ArrowTypeNode && ! ( expectedParameterType instanceof ArrowTypeNode ) )
				if ( parameter instanceof CallNode || parameter instanceof ClassCallNode )
					parameterType = ( ( ArrowTypeNode ) parameterType ).getRetType( );
				else throw TypeException.buildAndMark( "Passed value for " + ( i + 1 ) + "-th field is not of type " + expectedParameterType, lib );

			// check sub-typing of parameter
			if ( ! ( lib.isSubtype( parameterType, expectedParameterType ) ) )
				throw TypeException.buildAndMark( "Wrong type of " + ( i + 1 ) + "-th parameter in method '" + element.getID( ) + "' call", lib );
		}

		// return arrow-type-node
		return arrowNode.getRetType( );
	}

	@Override
	public Node visit( ClassNode element ) {
		// type-check every method
		for ( Node method : element.getMethods( ) ) visit( method );

		// additional checks if class extends
		if( element.getSuper( ) != null ) {
			ClassTypeNode superCTN = ( ClassTypeNode ) element.getSuper( ).getRetType( );
			ClassTypeNode thisCTN = ( ClassTypeNode ) element.getSymType( );

			for ( Node f : element.getFields( ) ) {
				int fieldOffset = -1 -( ( FieldNode ) f ).getOffset( );

				// check if field override parent's field. If override, check sub-typing
				if ( fieldOffset < superCTN.getFields( ).size( ) ) {
					FieldNode superField = ( FieldNode ) superCTN.getFields( ).get( fieldOffset );
					FieldNode myField = ( FieldNode ) thisCTN.getFields( ).get( fieldOffset );

					if ( ! lib.isSubtype( myField.getSymType( ), superField.getSymType( ) ) )
						throw TypeException.buildAndMark( "Overriding of field '" + superField.getID( ) + 
								"' has wrong type. Expected: " + superField.getSymType( ) + " (or super). Given: " + myField.getSymType( ), lib );
				}
			}

			for ( Node m : element.getMethods( ) ) {
				MethodNode method = ( MethodNode ) m;

				// check if method override parent's method. If override, check sub-typing
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
	public Node visit( ClassTypeNode element ) { return null; }

	@Override
	public Node visit( DivNode element ) {
		// check that operands are integers
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
	public Node visit( EmptyTypeNode element ) { return null; }

	@Override
	public Node visit( EqualNode element ) {
		Node left = visit( element.getLeft( ) );	// type-check left
		Node right = visit( element.getRight( ) );	// type-check right

		// cannot compare functional-type (design choice)
		if ( left instanceof ArrowTypeNode || right instanceof ArrowTypeNode )
			throw TypeException.buildAndMark( "Incompatible types in Equal comparison: cannot compare functional-types", lib );

		// cannot compare object with non-object/null
		if ( left instanceof RefTypeNode && ! ( right instanceof RefTypeNode || right instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Equal comparison", lib );
		if ( right instanceof RefTypeNode && ! ( left instanceof RefTypeNode || left instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Equal comparison", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( FieldNode element ) { return null; }

	@Override
	public Node visit( FunNode element ) {
		// type-check declarations
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );

		// check that expression return correct type (return-type)
		if ( ! lib.isSubtype( visit( element.getExpession( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Return-type mismatch in function " + element.getID( ), lib );
		return null;
	}

	@Override
	public Node visit( GreaterEqualNode element ) {
		Node left = visit( element.getLeft( ) );	// type-check left
		Node right = visit( element.getRight( ) );	// type-check right

		// cannot compare functional-type (design choice)
		if ( left instanceof ArrowTypeNode || right instanceof ArrowTypeNode )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison: cannot compare functional-types", lib );

		// cannot compare object with non-object/null
		if ( left instanceof RefTypeNode && ! ( right instanceof RefTypeNode || right instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison", lib );
		if ( right instanceof RefTypeNode && ! ( left instanceof RefTypeNode || left instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( IdNode element ) {
		// check that id is not a class identifier
		if ( element.getEntry( ).getRetType( ) instanceof ClassTypeNode )
			throw TypeException.buildAndMark( "ID '" + element.getID( ) + "' cannot be a class name", lib );

		return element.getEntry( ).getRetType( );
	}

	@Override
	public Node visit( IfNode element ) {
		// check that condition is a boolean
		if ( ! ( lib.isSubtype( visit( element.getCondition( ) ), new BoolTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "Condition in IF is not a boolean", lib );

		Node thenBranch = visit( element.getThenBranch( ) );	// type-check then branch
		Node elseBranch = visit( element.getElseBranch( ) );	// type-check else branch

		// try to find the lowest common ancestor and throw an exception if it doesn't exist
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
	public Node visit( IntTypeNode element ) { return null; }

	@Override
	public Node visit( LessEqualNode element ) {
		Node left = visit( element.getLeft( ) );	// type-check left
		Node right = visit( element.getRight( ) );	// type-check right

		// cannot compare functional-type (design choice)
		if ( left instanceof ArrowTypeNode || right instanceof ArrowTypeNode )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison: cannot compare functional-types", lib );

		// cannot compare object with non-object/null
		if ( left instanceof RefTypeNode && ! ( right instanceof RefTypeNode || right instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison", lib );
		if ( right instanceof RefTypeNode && ! ( left instanceof RefTypeNode || left instanceof EmptyTypeNode ) )
			throw TypeException.buildAndMark( "Incompatible types in Greater-Equal comparison", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( MethodNode element ) {
		// type-check every declaration
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );

		// check that expression return correct type (return-type)
		if ( ! lib.isSubtype( visit( element.getExpession( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Return-type mismatch in method " + element.getID( ), lib );
		return null;
	}

	@Override
	public Node visit( MinusNode element ) {
		// check that operands are integer (or sub-type)
		if ( ! ( lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "First element in subtraction is not an integer", lib );
		if ( ! ( lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) ) )
			throw TypeException.buildAndMark( "First element in subtraction is not an integer", lib );

		return new IntTypeNode( );
	}

	@Override
	public Node visit( NewNode element ) {
		ClassTypeNode classType = ( ClassTypeNode ) element.getEntry( ).getRetType( );
		List<Node> requiredFields = classType.getFields( );

		// passed fields have to be the same number of required fields
		if ( requiredFields.size( ) != element.getFields( ).size( ) )
			throw TypeException.buildAndMark( "Wrong number of parameters in " + element.getID( ) + " instantiation. " +
					requiredFields.size( ) + " required, " + element.getFields( ).size( ) + " given", lib );

		for ( int i = 0; i < element.getFields( ).size( ); i++ ) {
			Node field = element.getFields( ).get( i );
			Node requiredType = ( ( FieldNode ) requiredFields.get( i ) ).getSymType( );

			// get field 'real' type
			Node fieldType = visit( field );

			// if it's a functional-type, then i pass it's result as parameter
			if ( fieldType instanceof ArrowTypeNode )
				if ( field instanceof CallNode || field instanceof ClassCallNode )
					fieldType = ( ( ArrowTypeNode ) fieldType ).getRetType( );
				else throw TypeException.buildAndMark( "Passed value for " + ( i + 1 ) + "-th field is not of type " + requiredType, lib );

			// check sub-typing of field
			if ( ! ( lib.isSubtype( fieldType, requiredType ) ) )
				throw TypeException.buildAndMark( "Passed value for " + ( i + 1 ) + "-th field is not of type " + requiredType, lib );
		}

		return new RefTypeNode( element.getID( ) );
	}

	@Override
	public Node visit( NotNode element ) {
		Node result = visit( element.getExpression( ) );	// get type of expression

		// check that expression is a boolean
		if ( ! ( result instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Non-boolean type in NOT operation", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( OrNode element ) {
		Node left = visit( element.getLeft( ) );	// type-check left
		Node right = visit( element.getRight( ) );	// type-check right

		// check correct types of operands
		if ( ! ( left instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "First element in OR is not a boolean", lib );
		if ( ! ( right instanceof BoolTypeNode ) )
			throw TypeException.buildAndMark( "Second element in OR is not a boolean", lib );

		return new BoolTypeNode( );
	}

	@Override
	public Node visit( ParNode element ) { return null; }

	@Override
	public Node visit( PlusNode element ) {
		// check that operands are both integers (or sub-type)
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
		// type-check every declaration
		for ( Node declaration : element.getDeclarations( ) ) visit( declaration );

		return visit( element.getExpression( ) );
	}

	@Override
	public Node visit( ProgNode element ) {
		return visit( element.getExpression( ) );
	}

	@Override
	public Node visit( RefTypeNode element ) { return null; }

	@Override
	public Node visit( STEntry element ) { return null; }

	@Override
	public Node visit( TimesNode element ) {
		// check that operands are both integers (or sub-type)
		if ( ! lib.isSubtype( visit( element.getLeft( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "First element in multiplication is not an integer", lib );
		if ( ! lib.isSubtype( visit( element.getRight( ) ), new IntTypeNode( ) ) )
			throw TypeException.buildAndMark( "Second element in multiplication is not an integer", lib );

		return new IntTypeNode( );
	}

	@Override
	public Node visit( VarNode element ) {
		// check that passed value has expected type
		if ( ! lib.isSubtype( visit( element.getExpression( ) ), element.getSymType( ) ) )
			throw TypeException.buildAndMark( "Incompatible value for variable " + element.getID( ), lib );
		return null;
	}

}
