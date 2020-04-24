package virtual.machine;

import generated.SVMParser;

import static lib.FOOLLib.MEMSIZE;

/**
 * The basic (CLI) virtual machine on which run the assembly code
 * 
 * @author Mario Bravetti
 */
public class ExecuteVM {

	public static final int CODESIZE = 10000;

	private int[] code;
	private int[] memory = new int[MEMSIZE];

	private int ip = 0;
	private int sp = MEMSIZE;

	private int hp = 0;
	private int fp = MEMSIZE;
	private int ra;
	private int tm;

	public ExecuteVM( int[] code ) {
		this.code = code;
	}

	/**
	 * Executes the assembly code
	 */
	public void cpu( ) {
		while ( true ) {
			int bytecode = code[ip++];	// fetch instruction
			int v1, v2;
			int address;
			switch ( bytecode ) {
			case SVMParser.PUSH:	// push value on top of the stack
				push( code[ip++] );
				break;
			case SVMParser.POP:		// remove the top of the stack
				pop( );
				break;
			case SVMParser.ADD:		// sum the two value on top of the stack
				v1 = pop( );		// put the result on top of stack after removed the two operands
				v2 = pop( );
				push( v2 + v1 );
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
			case SVMParser.SUB:		// subtract the first value on top of the stack from the second one
				v1 = pop( );		// put the result on top of stack after removed the two operands
				v2 = pop( );
				push( v2 - v1 );
				break;
			case SVMParser.STOREW:	// consider the value at the top of the stack as an address
				address = pop( );	// remove the address and save in the memory pointed by it
				memory[address] = pop( );	// the value NOW on top of stack (and remove it from top)
				break;
			case SVMParser.LOADW:		// consider the value at the top of the stack as an address
				push( memory[pop( )] );	// remove the address and push the value pointed by it
				break;
			case SVMParser.BRANCH:		// jump to address specified in code
				address = code[ip];
				ip = address;
				break;
			case SVMParser.BRANCHEQ:	// if the value on top of the stack is equal to
				address = code[ip++];	// the second one, then jump to address specified in code
				v1 = pop( );
				v2 = pop( );
				if ( v2 == v1 )
					ip = address;
				break;
			case SVMParser.BRANCHLESSEQ:// if the value on top of the stack is greater than
				address = code[ip++];	// the second one, then jump to address specified in code
				v1 = pop( );
				v2 = pop( );
				if ( v2 <= v1 )
					ip = address;
				break;
			case SVMParser.JS:		// save the instruction pointer (to which return in the future) in ra
				address = pop( );	// and jump to sub-routine (address on top of the stack)
				ra = ip;
				ip = address;
				break;
			case SVMParser.STORERA:	// set ra to the value on top of the stack
				ra = pop( );
				break;
			case SVMParser.LOADRA:	// push value of ra on the stack
				push( ra );
				break;
			case SVMParser.STORETM:	// save top of the stack into tm
				tm = pop( );
				break;
			case SVMParser.LOADTM:	// push value of tm on the stack
				push( tm );
				break;
			case SVMParser.LOADFP:	// copy value of fp on the stack
				push( fp );
				break;
			case SVMParser.STOREFP: // save top of the stack into fp
				fp = pop( );
				break;
			case SVMParser.COPYFP:	// save in fp the sp
				fp = sp;
				break;
			case SVMParser.STOREHP:	// save top of the stack into hp
				hp = pop( );
				break;
			case SVMParser.LOADHP:	// put hp register value on the stack
				push( hp );
				break;
			case SVMParser.PRINT:	// print top of the stack
				System.out.println( ( sp < MEMSIZE) ? memory[sp] : "Empty stack!" );
				break;
			case SVMParser.HALT:	// exit program (execution)
				return;
			}
		}
	}

	private int pop( ) {
		return memory[sp++];
	}

	private void push( int v ) {
		memory[--sp] = v;
	}

}