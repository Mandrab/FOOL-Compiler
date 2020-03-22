package visitors;

import java.util.stream.Collectors;

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
import ast.NotNode;
import ast.OrNode;
import ast.ParNode;
import ast.PlusNode;
import ast.PrintNode;
import ast.ProgLetInNode;
import ast.ProgNode;
import ast.RefTypeNode;
import ast.STentry;
import ast.TimesNode;
import ast.VarNode;

public class PrinterVisitor extends ReflectionVisitor<String> implements NodeVisitor<String> {
	
	private String baseIndent;
	private int indentCount;
	
	public PrinterVisitor( ) {
		baseIndent = "\t";
		indentCount = 0;
	}
	
	public PrinterVisitor( String indent ) {
		baseIndent = indent;
		indentCount = 0;
	}

	@Override
	public String visit( AndNode element ) {
		String result = indent( ) + "And\n";
		incIndent( );
		result += element.getLeft( ).accept( this ) + element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ArrowTypeNode element ) {
		String result = indent( ) + "ArrowTypeNode\n";
		incIndent( );
		result += element.getParameters( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		result += element.getRetType( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( BoolNode element ) {
		return indent( ) + "Bool: " + element.getValue( ) + "\n";
	}

	@Override
	public String visit( BoolTypeNode element ) {
		return indent( ) + "Type: Bool\n";
	}

	@Override
	public String visit( CallNode element ) {
		String result = indent( ) + "Call: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getEntry( ).accept( this );
		result += indent( ) + "pars:\n" + element.getParameters( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ClassCallNode element ) {
		String result = indent( ) + "Class call of method: '" + element.getID( ) + "'\n";
		incIndent( );
		result += element.getParameters( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		result += element.getMethodEntry( ).getRetType( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ClassNode element ) {
		String result = indent( ) + "Class: " + element.getID( );
		if ( element.getSuper( ) != null )
			result += " extends " + element.getSuper( ).accept( this );
		incIndent( );
		result += "\n" + element.getFields( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		result += element.getMethods( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ClassTypeNode element ) {
		return null;
	}

	@Override
	public String visit( DivNode element ) {
		String result = indent( ) + "Div\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( EmptyNode element ) {
		return indent( ) + "Null\n"; 
	}

	@Override
	public String visit( EmptyTypeNode element ) {
		return indent( ) + "EmptyType\n";
	}

	@Override
	public String visit( EqualNode element ) {
		String result = indent( ) + "Equal\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( FieldNode element ) {
		String result = indent( ) + "Field: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getSymType( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( FunNode element ) {
		String result = indent( ) + "Fun: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getParameters( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		result += element.getDeclarations( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		result += element.getExpession( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( GreaterEqualNode element ) {
		String result = indent( ) + "GreaterEqual\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( IdNode element ) {
		String result = indent( ) + "ID: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getEntry( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( IfNode element ) {
		String result = indent( ) + "If\n";
		incIndent( );
		result += element.getCondition( ).accept( this );
		result += element.getThenBranch( ).accept( this );
		result += element.getElseBranch( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( IntNode element ) {
		return indent( ) + "Int:" + element.getValue( ) + "\n";
	}

	@Override
	public String visit( IntTypeNode element ) {
		return indent( ) + "IntType\n";
	}

	@Override
	public String visit( LessEqualNode element ) {
		String result = indent( ) + "LessEqual\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( MethodNode element ) {
		String result = indent( ) + "Method: " + element.getID( ) + "\n";
		incIndent( );
		result += indent( ) + "return:\n";
		incIndent( );
		result += element.getSymType( ).accept( this );
		decIndent( );
		result += indent( ) + "params:\n";
		incIndent( );
		result += element.getParameters( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		result += indent( ) + "vars:\n";
		incIndent( );
		result += element.getDeclarations( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		result += indent( ) + "return:\n";
		incIndent( );
		result += element.getExpession( ).accept( this );
		decIndent( ); decIndent( );
		return result;
	}

	@Override
	public String visit( MinusNode element ) {
		String result = indent( ) + "Minus\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( NewNode element ) {
		String result = indent( ) + "Class: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getFields( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		return result;
	}

	@Override
	public String visit( NotNode element ) {
		String result = indent( ) + "Not\n";
		incIndent( );
		result += element.getExpression( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( OrNode element ) {
		String result = indent( ) + "Or\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ParNode element ) {
		String result = indent( ) + "Parameter: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getSymType( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( PlusNode element ) {
		String result = indent( ) + "Plus\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( PrintNode element ) {
		String result = indent( ) + "Print\n";
		incIndent( );
		result += element.getExpression( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ProgLetInNode element ) {
		String result = indent( ) + "ProgLetIn\n";
		incIndent( );
		result += element.getDeclarations( ).stream( ).map( n -> n.accept( this ) ).collect( Collectors.joining( ) );
		decIndent( );
		return result;
	}

	@Override
	public String visit( ProgNode element ) {
		String result = indent( ) + "Prog\n";
		incIndent( );
		result += element.getExpression( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( RefTypeNode element ) {
		return indent( ) + "Reference of class: " + element.getID( ) + "\n" ;
	}

	@Override
	public String visit( STentry element ) {
		String result = indent( ) + "nesting level: " + element.getNestingLevel( ) + "\n";
		result += indent( ) + "offset: " + element.getOffset( ) + "\n";
		result += indent( ) + "type:\n";
		incIndent( );
		result += element.getRetType( ).accept( this );
		decIndent( );
		return result + indent( ) + "is " + ( element.isMethod( ) ? "" : "not " ) + "method\n";
	}

	@Override
	public String visit( TimesNode element ) {
		String result = indent( ) + "Times\n";
		incIndent( );
		result += element.getLeft( ).accept( this );
		result += element.getRight( ).accept( this );
		decIndent( );
		return result;
	}

	@Override
	public String visit( VarNode element ) {
		String result = indent( ) + "Var: " + element.getID( ) + "\n";
		incIndent( );
		result += element.getSymType( ).accept( this );
		result += element.getExpression( ).accept( this );
		decIndent( );
		return result;
	}
	
	private String incIndent( ) {
		indentCount++;
		StringBuilder builder = new StringBuilder( );
		for ( int i = 0; i < indentCount; i++ )
			builder.append( baseIndent );
		return builder.toString( );
	}
	
	private String indent( ) {
		StringBuilder builder = new StringBuilder( );
		for ( int i = 0; i < indentCount; i++ )
			builder.append( baseIndent );
		return builder.toString( );
	}
	
	private String decIndent( ) {
		indentCount--;
		StringBuilder builder = new StringBuilder( );
		for ( int i = 0; i < indentCount; i++ )
			builder.append( baseIndent );
		return builder.toString( );
	}
}
