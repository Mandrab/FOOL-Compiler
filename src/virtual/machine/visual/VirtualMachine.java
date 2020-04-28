package virtual.machine.visual;

import java.util.List;

public class VirtualMachine {

	public VirtualMachine( int[] code, int[] sourceMap, List<String> source ) {

		new VMView( new VMCore( code ), sourceMap, source );
	}
}
