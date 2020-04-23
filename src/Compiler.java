import java.io.BufferedWriter;
import java.io.FileWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import ast.Node;
import generated.FOOLLexer;
import generated.FOOLParser;
import lib.FOOLLib;
import visitors.CodeGeneratorVisitor;
import visitors.ParserVisitor;
import visitors.PrinterVisitor;
import visitors.TypeCheckerVisitor;

/**
 * Compiles FOOL program generating assembly code
 */
public class Compiler {

	/**
	 * Compile the specified file (with .fool extension) into assembly code
	 * 
	 * @param args
	 * 		must contains file's path
	 * @throws Exception
	 * 		if no .fool file is specified or a compile error occurs
	 */
	public static void main( String[] args ) throws Exception {

		// file name (path) is required
		if ( args.length == 0 )
			throw new IllegalArgumentException( );

		// compile FOOL-code into VM's assembly
		compile( args[0] );
	}

	/**
	 * Compile the specified file (with .fool extension) into assembly code
	 * 
	 * @param filePath
	 * 		the path to the file
	 * @throws Exception
	 * 		if no .fool file is specified or a compile error occurs
	 */
	public static void compile( String filePath ) throws Exception {

		// check path file validity
		if ( filePath == null || ! filePath.endsWith( ".fool" ) )
			throw new Exception( "Path does not point to any .fool file!" );
		
		// open .fool file
		CharStream chars = CharStreams.fromFileName( filePath );

		// pass data to lexer and generate tokens stream
        FOOLLexer lexer = new FOOLLexer( chars );
        CommonTokenStream tokens = new CommonTokenStream( lexer );

        // check for lexical errors and eventually throw an exception
        if ( lexer.lexicalErrors > 0 )
        	throw new Exception( "The program was not in the right format. Exiting the compilation process now" );

        // parse input token and check for syntax errors (eventually throwing an exception)
        FOOLParser parser = new FOOLParser( tokens );
        if ( parser.getNumberOfSyntaxErrors( ) > 0 )
        	throw new Exception( "The program has generated " + parser.getNumberOfSyntaxErrors( ) + " syntax errors" );

        // create a global utilities library to pass to the visitors
        final FOOLLib globalLib = new FOOLLib( );
        
        // generate abstract syntax tree and check for symbol table errors (eventually throwing an exception)
        ParserVisitor parserVisitor = new ParserVisitor( globalLib );
        Node ast = parserVisitor.visit( parser.prog( ) );        
        if ( parserVisitor.getSymbolTableError( ) > 0 )
        	throw new Exception( "The program has generated " + parserVisitor.getSymbolTableError( ) + " symbol table errors" );

        // visualize (console-print) the ast
        System.out.println("Visualizing AST...");
        PrinterVisitor printerVisitor = new PrinterVisitor( );
        System.out.println( printerVisitor.visit( ast ) );

        // type-check the ast bottom-up
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor( globalLib );
        System.out.println( "Type checking ok! Type of the program is: " 
        		+ printerVisitor.visit( typeCheckerVisitor.visit( ast ) ) );

        // generate the assembly code
        CodeGeneratorVisitor codeGeneratorVisitor = new CodeGeneratorVisitor( globalLib );
        String code = codeGeneratorVisitor.visit( ast );
        BufferedWriter out = new BufferedWriter( new FileWriter( filePath.substring( 0, filePath.indexOf( ".fool" ) ) + ".asm" ) ); 
        out.write( code );
        out.flush( );
        out.close( );

        System.out.println( "Code generated successfully!" );
	}
}
