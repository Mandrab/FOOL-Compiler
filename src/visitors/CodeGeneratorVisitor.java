package visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

public class CodeGeneratorVisitor extends ReflectionVisitor<String> implements NodeVisitor<String> {

	private static final String INC_HP = "push 1\n" + "lhp\n" + "add\n" + "shp\n";
	private final FOOLLib lib;

	public CodeGeneratorVisitor( FOOLLib globalLib ) {
		lib = globalLib;
	}

	@Override
	public String visit( Node element ) {
		try {
			return super.visit( element );
		} catch ( Exception e ) { e.printStackTrace( ); }

		return null;
	}

	/**
	 * Evaluate AND expression
	 * Multiply two boolean (0 false, 1 true):
	 * 		0 * 0 = 0 (false)
	 * 		0 * 1 = 0 (false)
	 * 		1 * 1 = 1 (true)
	 */
	@Override public String visit( AndNode element ) {
		return visit( element.getLeft( ) ) +
				visit( element.getRight( ) ) +
				"mult\n";
	}

	/**
	 * Arrow-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( ArrowTypeNode element ) { return null; }

	/**
	 * If boolean value is true return 1, otherwise return 0
	 */
	@Override public String visit( BoolNode element ) {
		return "push " + ( element.getValue( ) ? 1 : 0 ) + "\n";
	}

	/**
	 * Bool-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( BoolTypeNode element ) { return null; }

	/**
	 * Produce assembly code for routine call (function call or method call inside the class)
	 */
	@Override public String visit( CallNode element ) {
		String result = "lfp\n";	// push Control Link (pointer to frame of call)

		// generate code for parameter expressions in reversed order
		for ( int i = element.getParameters( ).size( ) - 1; i >= 0; i-- )
			result += visit( element.getParameters( ).get( i ) );

		result += "lfp\n";	// push Control Link used to ascend to declaration AR

		// find Access Link (pointer to frame of function's declaration, reached as for variable id)
		for ( int i = 0; i < element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ); i++ )
			result += "lw\n";

		// for a method, the label is found in dispatch table
		if ( element.getEntry( ).isMethod( ) )
			return result + 
					"stm\n" + "ltm\n" + "ltm\n" +	// duplicate top of the stack (contains AR of declaration)
					"lw\n" +						// get value (dispatch pointer)
			        "push " + element.getEntry( ).getOffset( ) + "\n"+ // push method offset
					"add\n" +						// get method's label address
		            "lw\n" + 						// get value (label of method's subroutine)
			        "js\n";							// jump to subroutine (put address of next instruction in ra)

		// for a function, the label is found in AR of declaration
		return result +
				"push " + element.getEntry( ).getOffset( ) + "\n" +	// push function offset
				"add\n" + 							// get function's declaration-AR's address
				"stm\n" + "ltm\n" +					// save top of stack in tm register
				"lw\n" +							// get value (AR address of function's declaration)
				"ltm\n" +							// put AR address again on stack
				"push 1\n" +						// label address is saved after the AR address in the stack
				"sub\n" +							// get function's label address
				"lw\n" +							// get value (label of function's subroutine)
				"js\n";								// jump to subroutine (put address of next instruction in ra)
	}

	/**
	 * Produce assembly code for method call
	 */
	@Override public String visit( ClassCallNode element ) {
		String result = "lfp\n";	// push Control Link (pointer to frame of function id caller)

		// generate code for parameter expressions in reversed order
		for ( int i = element.getParameters( ).size( ) - 1; i >= 0; i-- )
			result += visit( element.getParameters( ).get( i ) );

		result += "lfp\n";	// push Control Link used to ascend to declaration AR

		// Find the correct AR of (object) declaration.
		for ( int i = 0; i < element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ); i++ )
			result += "lw\n";

		return result +
				"push " + element.getEntry( ).getOffset( ) + "\n" +	// push object offset
				"add\n" +							// get object pointer's address
				"lw\n" +							// get value (address of object instance)
				"stm\n" + "ltm\n" + "ltm\n" +		// duplicate top of the stack (contains object pointer)
				"lw\n" +							// put dispatch pointer on stack
				"push " + element.getMethodEntry( ).getOffset( ) + "\n" +	// push method offset
				"add\n" +							// get method's label address
				"lw\n" +							// get value (label of method's subroutine)
				"js\n";								// jump to subroutine (put address of next instruction in ra)
	}

	/**
	 * Generate code for class definition
	 */
	@Override public String visit( ClassNode element ) {
		// list of methods' labels
		List<String> myDispatchTable = new ArrayList<>( );
		lib.addDispatchTable( myDispatchTable );

		int parentMethods = 0;

		// if extends, get labels of parent's methods
		if ( element.getSuper( ) != null ) {
			parentMethods = ( ( ClassTypeNode ) ( element.getSuper( ).getRetType( ) ) ).getMethods( ).size( );
			List<String> superLabel = lib.getDispatchTable( -2 -element.getSuper( ).getOffset( ) );

			for( String s : superLabel )
				myDispatchTable.add( s );
		}

		for( Node method : element.getMethods( ) ) {
			visit( method );	// generate code for each method

			String methodLabel = ( ( MethodNode ) method ).getLabel( );
			int methodOffset = ( ( MethodNode ) method ).getOffset( );

			// if offset belong to parent's method, then there's override (replace to the new label)
			// otherwise, simply add the method
			if ( methodOffset < parentMethods ) {
				myDispatchTable.remove( methodOffset );
				myDispatchTable.add( methodOffset, methodLabel );
			} else {
				myDispatchTable.add( methodLabel );	
			}
		}

		return "lhp\n" +
				myDispatchTable.stream( ).map( s -> // for each method
						"push " + s + "\n" +		// push the label
						"lhp\n" + "sw\n" +			// store label at address pointed by hp
						INC_HP						// increase hp
				).collect( Collectors.joining( ) );
	}

	/**
	 * Class-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( ClassTypeNode element ) { return null; }

	/**
	 * Generate code for division
	 */
	@Override public String visit( DivNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "div\n";
	}

	/**
	 * Generate code for 'null' address (equals to -1: is different by any other address)
	 */
	@Override public String visit( EmptyNode element ) {
		return "push -1\n";
	}

	/**
	 * Empty-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( EmptyTypeNode element ) { return null; }

	/**
	 * Generate code for equal compare
	 */
	@Override public String visit( EqualNode element ) {
		String equal = lib.freshLabel( );
		String end = lib.freshLabel( );

		return visit( element.getLeft( ) ) +	// get left value
				visit( element.getRight( ) ) +	// get right value
				"beq " + equal + "\n" + 		// if equals, jump to equals label ...
				"push 0\n" + 					// ... otherwise push 'false' ...
				"b " + end + "\n" + 			// ... then jump to end
				equal + ": \n" +
				"push 1\n" +					// push 'true'
				end + ": \n";
	}

	/**
	 * A field-node contains only information needed in type-check
	 */
	@Override public String visit( FieldNode element ) { return null; }

	/**
	 * Code generation for function definition
	 */
	@Override public String visit( FunNode element ) {
		final String functionLabel = lib.freshFunctionLabel( );	// generate function label

		final String result = functionLabel + ":\n" +
				"cfp\n" +							// copy sp into fp
				"lra\n" +							// push ra on stack
				element.getDeclarations( ).stream( )
				.map( this::visit )					// generate code for every declaration
				.collect( Collectors.joining( ) );

		final String popDeclarations = element.getDeclarations( ).stream( )
				.map( e -> e instanceof FunNode		// functional declaration use double size in memory (so needs two pop)
						? "pop\n" + "pop\n"			// pop declarations (functional ones)
						: "pop\n"					// pop declarations (non-functional ones)
				).collect( Collectors.joining( ) );

		// parameters push is done by call node

		final String popParameters = element.getParameters( ).stream( )
				.map( e -> ( ( DecNode ) e ).getSymType( ) instanceof ArrowTypeNode // functional parameters use double size in memory (so needs two pop)
						? "pop\n" + "pop\n"			// pop parameters (functional ones)
						: "pop\n"					// pop parameters (non-functional ones)
				).collect( Collectors.joining( ) );

		lib.putCode( result +
				visit( element.getExpession( ) ) +	// generate expression's code
				"stm\n" +							// store expression's result in tm
				popDeclarations +					// pop declarations (now unneeded)
				"sra\n" +							// restore ra
				"pop\n" +							// pop AL (access link)
				popParameters +						// pop parameters (now unneeded)
				"sfp\n" +							// reset fp to CL (caller AR)
				"ltm\n" +							// push function's result on top of the stack
				"lra\n" + "js\n"					// load return address and jump to it
		);

		return "lfp\n" +							// push AR of declaration
				"push " + functionLabel + "\n";		// push function label
	}

	/**
	 * Generate code for greater-equal node
	 */
	@Override public String visit( GreaterEqualNode element ) {
		final String lesserEqual = lib.freshLabel( );
		final String end = lib.freshLabel( );
		
		// NOTE that right and left code generation is swapped! A >= B --> B <= A
		return visit( element.getRight( ) ) +
				visit( element.getLeft( ) ) +
				"bleq " + lesserEqual + "\n" +		// if less-equal, jump to less-equal label ...
				"push 0\n" +						// ... otherwise push 'false' ...
				"b " + end + "\n" +					// ... then jump to end
				lesserEqual + ": \n" +
				"push 1\n" +						// push 'true' (greater-equal)
				end + ": \n";
	}

	/**
	 * Generate code for an 'id'
	 */
	@Override public String visit( IdNode element ) {
		// generate code for obtain the AR of declaration of id
		// ascend Control Links until AR of declaration
		String findAR = "lfp\n" +
				IntStream.range( 0, element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ) )
				.mapToObj( e -> "lw\n" ).collect( Collectors.joining( ) );

		// if it's not a functional id ...
		if ( ! ( element.getEntry( ).getRetType( ) instanceof ArrowTypeNode ) )
			return findAR +							// find AR of declaration
					"push " + element.getEntry( ).getOffset( ) + "\n" +	// push id offset 
					"add\n" +						// get address of value
					"lw\n";							// put value on top of the stack

		// if it's a functional id, then it occupies double space in memory (AL and function's label)
		return findAR +								// find AR of declaration of id
				"push " + element.getEntry( ).getOffset( ) + "\n" +	// push id offset
				"add\n" +							// get address of function's AL
				"stm\n" + "ltm\n" +               	// save top of stack in tm (contains address of id)
				"lw\n" +							// load AL on top of the stack
				"ltm\n" +							// put address of id on stack
				"push 1\n" +						// previous value on stack contains function's label
				"sub\n" +							// get function's label
				"lw\n";								// put on top of the stack the function's label
	}

	/**
	 * Generate code for if-then-else node
	 */
	@Override public String visit( IfNode element ) {
		String then = lib.freshLabel( );
	    String end = lib.freshLabel( );

	    return visit( element.getCondition( ) ) +	// get condition result (0 or 1)
	    		"push 1\n" +						// push 'true' to compare
				"beq " + then + "\n" +				// if condition is 'true', then jump to 'then-branch' ...
				visit( element.getElseBranch( ) ) +	// ... else execute code of 'else-branch' ...
				"b " + end + "\n" +					// ... and jump to end
				then + ": \n" +
				visit( element.getThenBranch( ) ) +	// execute code of 'then-branch'
				end + ": \n";	     
	}

	/**
	 * Push int value on top of the stack
	 */
	@Override public String visit( IntNode element ) {
		return "push " + element.getValue( ) + "\n";
	}

	/**
	 * Int-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( IntTypeNode element ) { return null; }

	/**
	 * Generate code for less-equal node
	 */
	@Override public String visit( LessEqualNode element ) {
		String lesserEqual = lib.freshLabel( );
	    String end = lib.freshLabel( );

	    return visit( element.getLeft( ) ) +
	    		visit( element.getRight( ) ) +
				"bleq " + lesserEqual + "\n" +	// if less-equal, jump to less-equal label ...
				"push 0\n" +					// ... otherwise push 'false' ...
				"b " + end + "\n" +				// ... then jump to end
				lesserEqual + ": \n" +
				"push 1\n" +					// push 'true' (greater-equal)
				end + ": \n";
	}

	/**
	 * Code generation for method definition
	 */
	@Override public String visit( MethodNode element ) {

		element.setLabel( lib.freshMethodLabel( ) );// generate a label for method

		String declarationsCode = element.getDeclarations( ).stream( )
				.map( this::visit )					// generate code for every declaration
				.collect( Collectors.joining( ) );

		// NOTE that methods cannot have functional declarations
		String removeDeclarationsCode = element.getDeclarations( ).stream( )
				.map( e -> "pop\n" )				// pop declarations
				.collect( Collectors.joining( ) );

		// parameters push is done by call/class-call node

		String removeParametersCode = element.getParameters( ).stream( )
				.map( e -> ( ( DecNode ) e ).getSymType( ) instanceof ArrowTypeNode	// functional parameters use double size in memory (so needs two pop)
						? "pop\n" + "pop\n"			// pop parameters (functional ones)
						: "pop\n"					// pop parameters (non-functional ones)
				).collect( Collectors.joining( ) );

		lib.putCode( element.getLabel( ) + ":\n" +	// insert method's start point
				"cfp\n" +							// copy sp into fp
				"lra\n" +							// push ra on stack
				declarationsCode +					// generate code for every declaration
				visit( element.getExpession( ) ) +	// generate expression's code
				"stm\n" +							// store expression's result in tm
				removeDeclarationsCode +			// pop declarations (now unneeded)
				"sra\n" +							// restore ra
				"pop\n" +							// pop AL (access link)
				removeParametersCode +				// pop parameters (now unneeded)
				"sfp\n" +							// reset fp to CL (caller AR)
				"ltm\n" +							// push function's result on top of the stack
				"lra\n" + "js\n"					// load return address and jump to it
		);

		return "";
	}

	/**
	 * Generate code for subtraction
	 */
	@Override public String visit( MinusNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "sub\n";
	}

	/**
	 * Generate code for object instantiation
	 */
	@Override public String visit( NewNode element ) {
		String fieldCode = element.getFields( ).stream( )
				.map( this::visit )					// generate code for every field
				.collect( Collectors.joining( ) );

		fieldCode += element.getFields( ).stream( )
				.map( e -> "lhp\n" +				// push hp
						"sw\n" +					// store second value on the stack in heap
						INC_HP						// increment hp
				).collect( Collectors.joining( ) );

		// get global AR (memsize) and add class offset
		int dispatchTableStackOffset = element.getEntry( ).getOffset( ) + FOOLLib.MEMSIZE; 
		fieldCode += "push " + dispatchTableStackOffset + "\n" + // push dispatch pointer's address 
				"lw\n" +							// put the dispatch pointer on top of the stack
				"lhp\n" +							// push hp on stack
				"sw\n" +							// store dispatch pointer on heap
				"lhp\n";							// put hp on stack

		return fieldCode + INC_HP;
	}

	/**
	 * Generate code for negate a boolean:
	 * 		1 - 0 = 1 (true)
	 * 		1 - 1 = 0 (false)
	 */
	@Override public String visit( NotNode element ) {
		return "push 1\n" +
				visit( element.getExpression( ) ) +
				"sub\n";
	}

	/**
	 * Evaluate OR expression
	 * If the sum of the two boolean is 0, then OR
	 * result is 'false', otherwise is 'true' (false = 0, true = 1)
	 */
	@Override public String visit( OrNode element ) {
		String False = lib.freshLabel( );
	    String end = lib.freshLabel( );

	    return visit( element.getLeft( ) ) +
	    		visit( element.getRight( ) ) +
	    		"add\n" +							// sum the two boolean (false = 0, true = 1)
				"push 0\n" +						// push 0 to compare
				"beq " + False + "\n" +				// if sum equals to 0, then jump to False ...
				"push 1\n" +						// otherwise push 'true' ...
				"b " + end + "\n" +					// ... and jump to end
				False + ": \n" +
				"push 0\n" +						// push 'false'
				end + ": \n";	 
	}

	/**
	 * A parameter-node contains only information needed in type-check
	 */
	@Override public String visit( ParNode element ) { return null; }

	/**
	 * Generate code for sum
	 */
	@Override public String visit( PlusNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "add\n";
	}

	/**
	 * Add print instruction to code
	 */
	@Override public String visit( PrintNode element ) {
		return visit( element.getExpression( ) ) + "print\n";
	}

	/**
	 * Generate code for program node (with let-in)
	 */
	@Override public String visit( ProgLetInNode element ) {
		return "push 0\n" +							// initial instruction
				element.getDeclarations( ).stream( )
					.map( this::visit )				// generate code for declarations
					.collect( Collectors.joining( ) ) +
				visit( element.getExpression( ) ) +	// generate code for expression
				"halt\n" +							// exit instruction
				lib.getCode( );						// subroutines code
	}

	/**
	 * Generate code for program node (without let-in)
	 */
	@Override public String visit( ProgNode element ) {
		return visit( element.getExpression( ) ) + "halt\n";
	}

	/**
	 * Reference-type-node is used only in type-check, so it doesn't produce assembly code
	 */
	@Override public String visit( RefTypeNode element ) { return null; }

	/**
	 * STEntry is not (directly) used in AST and so doesn't generate code
	 */
	@Override public String visit( STEntry visitable ) { return null; }

	/**
	 * Generate code for multiplication
	 */
	@Override public String visit( TimesNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "mult\n";
	}

	/**
	 * Generate variable code (evaluate expression)
	 */
	@Override public String visit( VarNode element ) {
		return visit( element.getExpression( ) );
	}

}
