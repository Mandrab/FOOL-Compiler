import java.nio.file.Files;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import generated.SVMVISLexer;
import generated.SVMVISParser;
import virtual.machine.ExecuteVM;
import virtual.machine.visual.VirtualMachine;

/**
 * Runs assembly code on the virtual machine
 * 
 * @author Paolo Baldini
 */
public class Runner {

	/**
	 * Run the specified file (with .asm extension) on the virtual machine.
	 * The '-gui' flag allow to run the GUI version of the Stack Virtual Machine.
	 * 
	 * @param args
	 *		must contains file's path and eventually
	 *		the '-gui' flag to run the GUI
	 * @throws Exception
	 *		if no .asm file is specified or a runtime error occurs
	 */
	public static void main( String[] args ) throws Exception {

		// file name(path) is required
		if ( args.length == 0 )
			throw new IllegalArgumentException( );

		// i can have a '-visual' flag to run GUI
		if ( args.length == 1 )
			runCode( args[0], false );
		if ( args.length == 2 && args[1].equals( "-gui" ) )
			runCode( args[0], true );
	}

	/**
	 * Run the specified file (with .asm extension) on the virtual machine.
	 * The visual parameter allow to run the GUI version of the Stack Virtual Machine.
	 * 
	 * @param filePath
	 *		the path to the .asm file
	 * @param visual
	 *		if true, run the GUI. Otherwise, run in CLI
	 * @throws Exception
	 *		if no .asm file is specified or a runtime error occurs
	 */
	public static void runCode( String filePath, boolean visual ) throws Exception {

		// check path file validity
		if ( filePath == null || ! filePath.endsWith( ".asm" ) )
			throw new Exception( "Path does not point to any .asm file!" );

		// open .asm (assembly) file
		CharStream charsASM = CharStreams.fromFileName( filePath );

		// validate code through SVM (Stack Virtual Machine)
		SVMVISLexer lexerASM = new SVMVISLexer( charsASM );
		CommonTokenStream tokensASM = new CommonTokenStream( lexerASM );
		SVMVISParser parserASM = new SVMVISParser( tokensASM );
		parserASM.assembly( );

		// check error in assembly code
		if ( lexerASM.lexicalErrors + parserASM.getNumberOfSyntaxErrors( ) > 0 )
			throw new Exception( "You had " + lexerASM.lexicalErrors + " lexical errors and " + parserASM.getNumberOfSyntaxErrors() + " syntax errors." );

		// run the code
		System.out.println( "Starting Virtual Machine..." );
		if ( visual )
			new VirtualMachine( parserASM.code, parserASM.sourceMap, Files.readAllLines( Paths.get( filePath ) ) );
		else new ExecuteVM( parserASM.code ).cpu( );
	}
}
