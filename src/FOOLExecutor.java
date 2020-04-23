import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * Compile and run FOOL programs
 * 
 * @author Paolo Baldini
 */
public class FOOLExecutor {

	private static final String DEFAULT_FILE_PATH = "fool_files" + File.separator + "quicksort_ho";

	/**
	 * Compile and run a program. If no file is specified, run a default one.
	 * The '-gui' flag allow to run the GUI version of the Stack Virtual Machine.
	 * 
	 * @param args
	 * 		may contains the path to the file (without .fool extension) and/or the
	 * 		'-gui' flag to run the GUI
	 * @throws Exception
	 * 		generic exception thrown if compiler (or runner) fails
	 */
	public static void main( String[] args ) throws Exception {

		// run GUI only if '-gui' flag is specified
		boolean runGui = Arrays.stream( args ).anyMatch( s -> s.equals( "-gui" ) );

		// try to take file's path
		Optional<String> filePath = Arrays.stream( args ).filter( s -> ! s.equals( "-gui" ) ).findAny( );

		// if file's path is not specified, set the default one
		if ( filePath.isEmpty( ) )
			filePath = Optional.of( DEFAULT_FILE_PATH );

		// compile fool file
		Compiler.compile( filePath.get( ) + ".fool" );

		// run compiled program
        Runner.runCode( filePath.get( ) + ".asm", runGui );
	}
}