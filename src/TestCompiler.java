import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

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

	private <T> void testFool( String foolFile, Function<String,T> resultMapper, T expectedResult ) throws Exception {
		
		String fileName = FOOL_FILES_PATH + foolFile;

        Compiler.compile( fileName + ".fool" );

        redirectIO( );

        Runner.runCode( fileName + ".asm", false );

        restoreIO( );

        assertEquals( expectedResult, resultMapper.apply( testStream.toString( ) ) );
	}
	
	private void redirectIO( ) {
		// Create a stream to hold the output
		testStream = new ByteArrayOutputStream( );
		PrintStream ps = new PrintStream( testStream );
		// IMPORTANT: Save the old System.out!
		oldStream = System.out;
		// Tell Java to use your special stream
		System.setOut( ps );
	}

	private void restoreIO( ) {
		// Put things back
		System.out.flush( );
		System.setOut( oldStream );
	}
}
