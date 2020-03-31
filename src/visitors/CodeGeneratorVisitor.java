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
		} catch ( Exception e ) { e.printStackTrace(); }

		return null;
	}
	
	@Override
	public String visit( AndNode element ) {
		return visit( element.getLeft( ) ) +
				visit( element.getRight( ) ) +
				 "mult\n";	   // 1 per True, 0 per False.
	}

	@Override
	public String visit( ArrowTypeNode element ) {
		return null;
	}

	@Override
	public String visit( BoolNode element ) {
		return "push " + ( element.getValue( ) ? 1 : 0 ) + "\n";
	}

	@Override
	public String visit( BoolTypeNode element ) {
		return null;
	}

	@Override
	public String visit( CallNode element ) {
		String result = "lfp\n"; // push Control Link (pointer to frame of function id caller)

		// generate code for parameter expressions in reversed order
		for ( int i = element.getParameters( ).size( ) - 1; i >= 0; i-- )
			result += visit( element.getParameters( ).get( i ) );

		result += "lfp\n";
		
		// push Access Link (pointer to frame of function id declaration, reached as for variable id)
		for ( int i = 0; i < element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ); i++ )
			result += "lw\n";

		if ( element.getEntry( ).isMethod( ) ) {
			return result + 
					"stm\n" + "ltm\n" + "ltm\n" + // duplicate top of the stack
					"lw\n" + //vado nella DT
			        "push " + element.getEntry( ).getOffset( ) + "\n"+ //pusho indirizzo della funzione recuperato a offset ID
					"add\n" +
		            "lw\n" + // push function address (value at: pointer to frame of function id declaration + its offset)
			        "js\n";// jump to popped address (putting in $ra address of subsequent instruction)
		} else {
			return result +
					"push " + element.getEntry( ).getOffset( ) + "\n" + // push indirizzo ad AR dichiarazione funzione, recuperato a offset ID
					"add\n" + 
					"stm\n" + // duplicate top of the stack.
					"ltm\n" +
					"lw\n" + 
					"ltm\n" + // ripusho l'indirizzo ottenuto precedentemente, per poi calcolarmi offset ID - 1
					"push 1\n" + // push 1, 
					"sub\n" + // sottraggo a offset ID - 1, per recuperare l'indirizzo funzione.
					"lw\n" + // push function address.
					"js\n";
		}
	}

	@Override
	public String visit( ClassCallNode element ) {
		String result = "lfp\n";// push Control Link (pointer to frame of function id caller)
		// generate code for parameter expressions in reversed order
		for ( int i = element.getParameters( ).size( ) - 1; i >= 0; i-- )
			result += visit( element.getParameters( ).get( i ) );

		result += "lfp\n";
		// Find the correct AR address.
		for ( int i = 0; i < element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ); i++ )
			result += "lw\n";

		return result +
				"push " + element.getEntry( ).getOffset( ) + "\n" + // push indirizzo ad AR dichiarazione funzione, recuperato a offset ID 
				"add\n" + //Cos� ho l'Obj Pointer dell'obj nell'AR
				"lw\n" +//Carico sullo stack
				"stm\n" + "ltm\n" + "ltm\n" + // duplico il val sullo stack
				"lw\n" + // stack <- indirizzo dt --- paul (?)
				"push " + element.getMethodEntry( ).getOffset( ) + "\n"+
				"add\n" +
				"lw\n" + //Carico sullo stack
				"js\n"; //Salto all'indirizzo
	}

	@Override
	public String visit( ClassNode element ) {
		List<String> myDispatchTable = new ArrayList<>( );
		lib.addDispatchTable( myDispatchTable );

		int parentMethods = 0;
		if ( element.getSuper( ) != null ) {
			parentMethods = ( ( ClassTypeNode ) ( element.getSuper( ).getRetType( ) ) ).getMethods( ).size( );
			List<String> superLabel = lib.getDispatchTable( -2 -element.getSuper( ).getOffset( ) );
			
			for( String s : superLabel )
				myDispatchTable.add( s );
		}
		
		for( Node method : element.getMethods( ) ) {
			visit( method );

			String methodLabel = ( ( MethodNode ) method ).getLabel( );
			int methodOffset = ( ( MethodNode ) method ).getOffset( );

			if ( methodOffset < parentMethods ) { // override
				myDispatchTable.remove( methodOffset );
				myDispatchTable.add( methodOffset, methodLabel );
			} else {
				myDispatchTable.add( methodLabel );	
			}
		}

		return "lhp\n" + myDispatchTable.stream( ).map( s -> "push " + s + "\n" + //push label
				"lhp\n" + //push hp
				"sw\n" +
				INC_HP ).collect( Collectors.joining( ) );
	}

	@Override
	public String visit( ClassTypeNode element ) {
		return null;
	}

	@Override
	public String visit( DivNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "div\n";
	}

	@Override
	public String visit( EmptyNode element ) {
		return "push -1\n"; // -1 e' certamente diverso da qualsiasi indirizzo sullo stack
	}

	@Override
	public String visit( EmptyTypeNode element ) {
		return null;
	}

	@Override
	public String visit( EqualNode element ) {
		String l1 = lib.freshLabel( );
		String l2 = lib.freshLabel( );
		return visit( element.getLeft( ) ) + 
				visit( element.getRight( ) ) + 
				"beq " + l1 + "\n" + 
				"push 0\n" + 
				"b " + l2 + "\n" + 
				l1 + ": \n" + 
				"push 1\n" + 
				l2 + ": \n";
	}

	@Override
	public String visit(FieldNode element) {
		return null;
	}

	@Override
	public String visit( FunNode element ) {
		final String functionLabel = lib.freshFunctionLabel( );

		final String result = functionLabel + ":\n" + "cfp\n" + // setta il registro $fp / copy stack pointer into frame pointer
				"lra\n" + // load from ra sullo stack
				element.getDeclarations( ).stream( ).map( this::visit ).collect( Collectors.joining( ) );
		
		final String remdeclCode = element.getDeclarations( ).stream( ).map( e -> e instanceof FunNode 
				? "pop\n" + "pop\n" //pop del codice dichiarazione se funzionale + pop del codice dichiarazione
				: "pop\n" ).collect( Collectors.joining( ) );
		
		final String parCode = element.getParameters( ).stream( ).map( e -> ( ( DecNode ) e ).getSymType( ) instanceof ArrowTypeNode 
				? "pop\n" + "pop\n" //pop dei parametri se funzionale + pop dei parametri
				: "pop\n" ).collect( Collectors.joining( ) );

		lib.putCode( result +
				visit( element.getExpession( ) ) + "stm\n" + // salvo il risultato in un registro
				remdeclCode + // devo svuotare lo stack, e faccio pop tanti quanti sono le var/fun dichiarate
				"sra\n" + // salvo il return address
				"pop\n" + // pop dell'AL (access link)
				parCode + // pop dei parametri che ho in parlist
				"sfp\n" + // ripristino il registro $fp al CL, in maniera che sia l'fp dell'AR del
							// chiamante.
				"ltm\n" + "lra\n" + "js\n" // js salta all'indirizzo che � in cima allo stack e salva la prossima
											// istruzione in ra.
		);

		return "lfp\n" + "push " + functionLabel + "\n";
	}

	@Override
	public String visit( GreaterEqualNode element ) {
		final String l1 = lib.freshLabel( );
		final String l2 = lib.freshLabel( );
		return visit( element.getRight( ) ) +
				visit( element.getLeft( ) ) +
				"bleq " + l1 + "\n" +
				"push 0\n" + //in caso negativo pusho 0 (false)
				"b " + l2 + "\n" +
				l1 + ": \n" +
				"push 1\n" + //in caso positivo pusho 1 (true)
				l2 + ": \n";
	}

	@Override
	public String visit( IdNode element ) {
		String getAR = IntStream.range( 0, element.getNestingLevel( ) - element.getEntry( ).getNestingLevel( ) ).mapToObj( e -> "lw\n" ).collect( Collectors.joining( ) );

		if ( ! ( element.getEntry( ).getRetType( ) instanceof ArrowTypeNode ) ) {
			return "lfp\n" +	// AL
					getAR +		// Andiamo nel suo AR. getAr ci da l'AL.
					"push " + element.getEntry( ).getOffset( ) + "\n" +	// e aggiungiamo 
					"add\n" +
					"lw\n";
		} else {
			/* ArrowTypeNode
			 * qualsiasi ID con tipo funzionale (vero ID di funzione oppure
			 * ID di variabile o parametro di tipo funzione) occupa un offset doppio:
			 * [a offset messo in symbol table  ] FP ad AR dichiarazione funzione
			 * [a offset messo in symbol table-1] indir funzione (per invocazione suo codice)
			 */
			return  // Salviamo sullo Stack l'FP ad AR dichiarazione funzione. FP del frame dove � dichiarata la funzione.
					"lfp\n" +
					getAR +		// Andiamo nel suo AR. getAR ci da l'AL.
					"push " + element.getEntry( ).getOffset( ) + "\n" +
					"add\n" +
					"lw\n" +
					// Salviamo ora sullo Stack l'indir della funzione (per invocazione del suo codice). La sua label.
					"lfp\n" +
					getAR +		// Andiamo nel suo AR. getAR ci da l'AL.
					"push " + element.getEntry( ).getOffset( ) + "\n" +
					"push 1\n" +
					"sub\n" +
					"add\n" +
					"lw\n";	// Mettiamo sullo stack l'indirizzo della funzione (di nouvo). Ma non sono sicuro.
		}
	}

	@Override
	public String visit( IfNode element ) {
		String l1 = lib.freshLabel( );
	    String l2 = lib.freshLabel( );
	    return visit( element.getCondition( ) ) +
	    		"push 1\n" +
				"beq " + l1 + "\n" +				 				  
				visit( element.getElseBranch( ) ) +
				"b " + l2 + "\n" +
				l1 + ": \n" +
				visit( element.getThenBranch( ) ) +
				l2 + ": \n";	     
	}

	@Override
	public String visit( IntNode element ) {
		return "push " + element.getValue( ) + "\n";
	}

	@Override
	public String visit( IntTypeNode element ) {
		return null;
	}

	@Override
	public String visit( LessEqualNode element ) {
		String l1= lib.freshLabel();
	    String l2= lib.freshLabel();
	    return visit( element.getLeft( ) ) +
	    		visit( element.getRight( ) ) +
				"bleq " + l1 + "\n" +
				"push 0\n" + //in caso negativo pusho 0 (false)
				"b " + l2 + "\n" +
				l1 + ": \n" +
				"push 1\n" + //in caso positivo pusho 1 (true)
				l2 + ": \n";	         
	}

	@Override
	public String visit( MethodNode element ) {

		element.setLabel( lib.freshMethodLabel( ) );
		
		String declarationsCode = element.getDeclarations( ).stream( ).map( this::visit ).collect( Collectors.joining( ) );
		
		String removeDeclarationsCode = element.getDeclarations( ).stream( ).map( e -> e instanceof MethodNode
				? "pop\n" + "pop\n" //pop del codice dichiarazione se funzionale + pop del codice dichiarazione
				: "pop\n" ).collect( Collectors.joining( ) );

		String removeParametersCode = element.getParameters( ).stream( ).map( e -> ( ( DecNode ) e ).getSymType( ) instanceof ArrowTypeNode
				? "pop\n" + "pop\n"
				: "pop\n" ).collect( Collectors.joining( ) );

		lib.putCode(
				element.getLabel( ) + ":\n" + 
				"cfp\n" + // setta il registro $fp / copy stack pointer into frame pointer
				"lra\n" + // load from ra sullo stack
				declarationsCode + // codice delle dichiarazioni
				visit( element.getExpession( ) ) + "stm\n" + // salvo il risultato in un registro
				removeDeclarationsCode + // devo svuotare lo stack, e faccio pop tanti quanti sono le var/fun dichiarate
				"sra\n" + // salvo il return address
				"pop\n" + // pop dell'AL (access link)
				removeParametersCode + // pop dei parametri che ho in parlist
				"sfp\n" + // ripristino il registro $fp al CL, in maniera che sia l'fp dell'AR del
							// chiamante.
				"ltm\n" + "lra\n" + "js\n" // js salta all'indirizzo che � in cima allo stack e salva la prossima
											// istruzione in ra.
		);
			
		return "";
	}

	@Override
	public String visit( MinusNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "sub\n";
	}

	@Override
	public String visit( NewNode element ) {
		//fa push dei valori sullo stack
		String fieldCode = element.getFields( ).stream( ).map( this::visit ).collect( Collectors.joining( ) );
		
		fieldCode += element.getFields( ).stream( ).map( e -> "lhp\n" + //push hp
				"sw\n" + 
				"push 1\n" + "lhp\n" + "add\n" + "shp\n" ).collect( Collectors.joining( ) ); //hp++	
		
		fieldCode += "push " + ( element.getEntry( ).getOffset( ) + FOOLLib.MEMSIZE ) + "\n" + // push DP
				"lw\n" +
				"lhp\n" +
				"sw\n" +
				"lhp\n";

		return fieldCode + "push 1\n" + "lhp\n" + "add\n" + "shp\n";
	}

	@Override
	/*
	 * Per fare il not di un booleano pusha 1, poi il valore del booleano (0 false,
	 * 1 true) e poi sottrae. Cos� se era true (1) con la sottrazione fa a 0
	 * (false). Viceversa se era false (0) con la sottrazione va a 1 (true).
	 */
	public String visit( NotNode element ) {
		return "push 1\n" +
				visit( element.getExpression( ) ) +
				"sub\n";
	}

	@Override
	/*
	 * crea due etichette fresh. Per vedere se i due valori di codeGeneration sono
	 * uguali (booleani dove 0 = false e 1 = true) allora basta SOMMARE i due
	 * valori. Infatti nell'or se la somma fa 0 significa che sono entrambi 0 e
	 * quindi torner� false. Dopodich� confronta questa somma con 0 che viene
	 * pushato e se sono uguali allora l'or � false e salta a l1 dove viene pushato
	 * 0 che � il valore di ritorno false, se sono diversi allora � true, pusha 1
	 * che � il valore di ritorno true e salta a l2.
	 * 
	 * La logica � invertita rispetto all'AND.
	 */
	public String visit( OrNode element ) {
		String l1= lib.freshLabel( );
	    String l2= lib.freshLabel( );
	    return visit( element.getLeft( ) ) +
	    		visit( element.getRight( ) ) +
	    		"add\n" +
				"push 0\n" +
				"beq " + l1 + "\n" +
				"push 1\n" +
				"b " + l2 + "\n" +
				l1 + ": \n" +
				"push 0\n" +
				l2 + ": \n";	 
	}

	@Override
	public String visit( ParNode element ) {
		return null;
	}

	@Override
	public String visit( PlusNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "add\n";
	}

	@Override
	public String visit( PrintNode element ) {
		return visit( element.getExpression( ) ) + "print\n";
	}

	@Override
	public String visit( ProgLetInNode element ) {
		return "push 0\n" +
				element.getDeclarations( ).stream( ).map( this::visit ).collect( Collectors.joining( ) ) +
				visit( element.getExpression( ) ) +
				"halt\n" +
				lib.getCode( );
	}

	@Override
	public String visit( ProgNode element ) {
		return visit( element.getExpression( ) ) + "halt\n";
	}

	@Override
	public String visit( RefTypeNode element ) {
		return null;
	}
	
	@Override
	public String visit( STEntry visitable ) {
		return null;
	}

	@Override
	public String visit( TimesNode element ) {
		return visit( element.getLeft( ) ) + visit( element.getRight( ) ) + "mult\n";
	}

	@Override
	public String visit(VarNode element) {
		return visit( element.getExpression( ) );
	}

}
