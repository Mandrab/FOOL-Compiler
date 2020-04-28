import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import lib.TypeException;

/**
 * Tests the compiler by comparing outputs of compiled programs with expected results
 * 
 * @author Paolo Baldini
 */
class TestCompiler {

	private static final String RUNNER_LOGS = "Starting Virtual Machine...\n";
	private static final String FOOL_FILES_PATH = "fool_files" + File.separator;

	private ByteArrayOutputStream testStream;
	private PrintStream oldStream;

	@Test
	public void testQuicksort( ) throws Exception {
		testFool( "quicksort", s -> s, RUNNER_LOGS + "1\n2\n2\n3\n4\n5\n" );
	}

	@Test
	public void testQuicksortReverse( ) throws Exception {
		testFool( "quicksort_ho", s -> s, RUNNER_LOGS + "5\n4\n3\n2\n2\n1\n" );
	}

	@Test
	public void testBankLoan( ) throws Exception {
		testFool( "bankloan", s -> s, RUNNER_LOGS + "50000\n" );
	}

	@Test
	public void testLinSum( ) throws Exception {
		testFool( "linsum", s -> s, RUNNER_LOGS + "24\n" );
	}

	@Test
	public void testAverage( ) throws Exception {
		testFool( "test_06", s -> s, RUNNER_LOGS + "-1\n" );
		testFool( "test_07", s -> s, RUNNER_LOGS + "5\n" );
		testFool( "test_08", s -> s, RUNNER_LOGS + "2\n" );
		testFool( "test_09", s -> s, RUNNER_LOGS + "11\n" );
		testFool( "test_10", s -> s, RUNNER_LOGS + "0\n" );
		testFool( "test_11", s -> s, RUNNER_LOGS + "0\n" );
		testFool( "test_12", s -> s, RUNNER_LOGS + "0\n" );
	}

	@Test
	public void testBasic( ) throws Exception {
		testFool( "test_00", s -> s, RUNNER_LOGS + "8\n" );
		testFool( "test_01", s -> s, RUNNER_LOGS + "2\n" );
		testFool( "test_02", s -> s, RUNNER_LOGS + "10\n" );
		testFool( "test_03", s -> s, RUNNER_LOGS + "250\n" );
		testFool( "test_04", s -> s, RUNNER_LOGS + "0\n" );
		testFool( "test_05", s -> s, RUNNER_LOGS + "1\n" );
	}

	@Test
	public void testMath( ) throws Exception {
		testFool( "test_math_00", s -> s, RUNNER_LOGS + "-6\n" );
		testFool( "test_math_01", s -> s, RUNNER_LOGS + "1\n" );
		testFool( "test_math_02", s -> s, RUNNER_LOGS + "10\n" );
		testFool( "test_math_03", s -> s, RUNNER_LOGS + "4\n" );
		testFool( "test_math_04", s -> s, RUNNER_LOGS + "40\n" );
		testFool( "test_math_05", s -> s, RUNNER_LOGS + "4\n" );
	}

	@Test
	public void testExceptions( ) {
		testException( "test_type_exception_00", true );
		testException( "test_type_exception_01", true );
		testException( "test_type_exception_02", true );
		testException( "test_type_exception_03", true );
		testException( "test_type_exception_04", true );
		testException( "test_type_exception_05", true );
		testException( "test_type_exception_06", false );	// symbol table exception
		testException( "test_type_exception_07", false );	// symbol table exception
		testException( "test_type_exception_08", false );	// symbol table exception
		testException( "test_type_exception_09", true );	// lowest common ancestor test
		testException( "test_type_exception_10", true );
		testException( "test_type_exception_11", true );
	}

	/**
	 * Test program comparing provided result with expected one
	 * 
	 * @param <T>
	 *		type of result (converted from string): I might want an obj
	 * @param foolFile
	 *		file to compile, run and test
	 * @param resultMapper
	 *		map string result to required type
	 * @param expectedResult
	 *		the expected result
	 * @throws Exception
	 *		classical compile or run exception
	 */
	private <T> void testFool( String foolFile, Function<String,T> resultMapper, T expectedResult ) throws Exception {

		String fileName = FOOL_FILES_PATH + foolFile;

		Compiler.compile( fileName + ".fool" );

		// set to catch result
		redirectIO( );

		Runner.runCode( fileName + ".asm", false );

		// reset System.out
		restoreIO( );

		assertEquals( expectedResult, resultMapper.apply( testStream.toString( ) ) );
	}

	/**
	 * Redirect System.out to test-output-stream
	 */
	private void redirectIO( ) {
		// Create a stream to hold the output
		testStream = new ByteArrayOutputStream( );
		PrintStream ps = new PrintStream( testStream );
		// IMPORTANT: Save the old System.out!
		oldStream = System.out;
		// Tell Java to use your special stream
		System.setOut( ps );
	}

	/**
	 * Test compiler/program to check if it launches expected exceptions
	 * 
	 * @param file
	 *		file to test (build/run)
	 * @param expectedTypeException
	 *		true if i expect a type-exception,
	 *		false otherwise (i expect a different type of exception)
	 */
	public void testException( String file, boolean expectedTypeException ) {
		try {
			testFool( file, s -> s, RUNNER_LOGS + "" );
		} catch ( TypeException e ) {
			// if i don't expect a type-exception, then there was an unexpected error
			if ( expectedTypeException ) return;
			fail( );
		} catch ( Exception e ) {
			// if i expect a type-exception, then there was an unexpected error
			if ( expectedTypeException ) fail( );
			return;
		}
	}

	/**
	 * Restore System.out to default output-stream
	 */
	private void restoreIO( ) {
		// Put things back
		System.out.flush( );
		System.setOut( oldStream );
	}
}
