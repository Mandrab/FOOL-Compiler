package virtual.machine.visual;

import generated.SVMParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lib.FOOLLib.MEMSIZE;

/**
 * The core of the visual virtual machine on which run the assembly code
 * 
 * @author Paolo Baldini
 */
public class VMCore {

	private static final int CODESIZE = 10000;

	private List<VMState> oldStates;
	private VMState state;

	private int[] code;

	private boolean ended;

	public VMCore( int[] code ) {

		this.code = code;
		this.oldStates = new LinkedList<VMState>( );
		this.state = new VMState( new int[MEMSIZE], 0, MEMSIZE, 0, 0, 0, MEMSIZE, Optional.empty( ) );
	}

	/**
	 * Make a forward step, i.e., execute next instruction
	 */
	public void nextStep( ) {
		try {
			if ( ! ended ) {

				oldStates.add( 0, state );
				state = new VMState( state );

				int bytecode = fetch( );	// fetch instruction
				int v1, v2;
				int address;
				switch ( bytecode ) {
					case SVMParser.PUSH:	// push value on top of the stack
						v1 = fetch( );
						push( v1 );
						break;
					case SVMParser.POP:		// remove the top of the stack
						pop( );
						break;
					case SVMParser.ADD:		// sum the two value on top of the stack
						v1 = pop( );		// put the result on top of stack after removed the two operands
						v2 = pop( );
						push( v2 + v1 );
						break;
					case SVMParser.SUB:		// subtract the first value on top of the stack from the second one
						v1 = pop( );		// put the result on top of stack after removed the two operands
						v2 = pop( );
						push( v2 - v1 );
						break;
					case SVMParser.MULT:	// multiply the two value on top of the stack
						v1 = pop( );		// put the result on top of stack after removed the two operands
						v2 = pop( );
						push( v2 * v1 );
						break;
					case SVMParser.DIV:		// divide the second value from the top of the stack by the value on top of the stack
						v1 = pop( );		// put the result on top of stack after removed the two operands
						v2 = pop( );
						push( v2 / v1 );
						break;
					case SVMParser.STOREW:	// consider the value at the top of the stack as an address
						address = pop( );	// remove the address and save in the memory pointed by it
						state.getMemory( )[address] = pop( );
						break;
					case SVMParser.LOADW:	// consider the value at the top of the stack as an address
						push( state.getMemory( )[pop( )] );	// remove the address and push the value pointed by it
						break;
					case SVMParser.BRANCH:	// jump to address specified in code
						state.setIp( code[state.getIp( )] );
						break;
					case SVMParser.BRANCHEQ:// if the value on top of the stack is equal to
						address = fetch( );	// the second one, then jump to address specified in code
						v1 = pop( );
						v2 = pop( );
						state.setIp( v2 == v1 ? address : state.getIp( ) );
						break;
					case SVMParser.BRANCHLESSEQ:	// if the value on top of the stack is greater than
						address = fetch( );	// the second one, then jump to address specified in code
						v1 = pop( );
						v2 = pop( );
						state.setIp( v2 <= v1 ? address : state.getIp( ) );
						break;
					case SVMParser.JS:		// save the instruction pointer (to which return in the future) in ra
						address = pop( );	// and jump to sub-routine (address on top of the stack)
						state.setRa( state.getIp( ) );
						state.setIp( address );
						break;
					case SVMParser.LOADRA:	// push value of ra on the stack
						push( state.getRa( ) );
						break;
					case SVMParser.STORERA:	// set ra to the value on top of the stack
						state.setRa( pop( ) );
						break;
					case SVMParser.LOADTM:	// push value of tm on the stack
						push( state.getTm( ) );
						break;
					case SVMParser.STORETM:	// save top of the stack into tm
						state.setTm( pop( ) );
						break;
					case SVMParser.LOADFP:	// copy value of fp on the stack
						push( state.getFp( ) );
						break;
					case SVMParser.STOREFP:	// save top of the stack into fp
						state.setFp( pop( ) );
						break;
					case SVMParser.COPYFP:	// save in fp the sp
						state.setFp( state.getSp( ) );
						break;
					case SVMParser.LOADHP:	// put hp register value on the stack
						push( state.getHp( ) );
						break;
					case SVMParser.STOREHP:	// save top of the stack into hp
						state.setHp( pop( ) );
						break;
					case SVMParser.PRINT:	// print top of the stack
						state.addResult( state.getSp( ) == MEMSIZE ? "EMPTY STACK" : Integer.toString( state.getMemory( )[ state.getSp( ) ] ) );
						break;
					case SVMParser.HALT:	// exit program (execution)
						state = oldStates.remove( 0 );
						ended = true;
				}
			}

			// if overflow of stack/heap throws an exception
			if ( state.getHp( ) > state.getSp( ) ) {
				state.setResult( "Segmentation fault. " );
				throw new IllegalStateException( );
			}

			// if jumped into illegal area of memory-code throws an exception
			if ( state.getIp( ) < 0 || state.getIp( ) > CODESIZE ) {
				state.setResult( "Invalid value in Instruction Pointer (IP) register. " );
				throw new IllegalStateException( );
			}

		// if an exception occurs, restore previous state of the vm and block
		} catch ( Exception e ) {
			state.addResult( "VM state when crash occurred was:\n" + state );
			state = oldStates.remove( 0 );
			ended = true;
			e.printStackTrace( );
		}
	}

	/**
	 * Make a back-step, i.e., restore previous state of vm
	 */
	public void backStep( ) {
		if ( oldStates.size( ) > 0 )
			state = oldStates.remove( 0 );
		ended = false;
	}

	/**
	 * Get actual state
	 * 
	 * @return
	 * 		actual state of the vm
	 */
	public VMState getState( ) {
		return state;
	}

	public boolean hasEnded( ) {
		return ended;
	}

	private void push( int v ) {
		state.getMemory( )[state.decSp( )] = v;
	}

	private int pop( ) {
		int code = state.getMemory( )[ state.getSp( ) ];
		state.incSp( );
		return code;
	}

	private int fetch( ) {
		int code = this.code[ state.getIp( ) ];
		state.incIp( );
		return code;
	}

	public static int getCodeSize( ) {
		return CODESIZE;
	}
}