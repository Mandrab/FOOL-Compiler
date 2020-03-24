import java.io.File;

public class TestManual {

	private static final String FILE_PATH = "fool_files" + File.separator + "test";
	
	public static void main( String[] args ) throws Exception {

		// compile fool file
		Compiler.compile( FILE_PATH + ".fool" );

		// run compiled program
        Runner.runCode( FILE_PATH + ".asm", false );
	}
}