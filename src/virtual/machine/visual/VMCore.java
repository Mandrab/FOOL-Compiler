package virtual.machine.visual;

import generated.SVMParser;

import java.util.LinkedList;
import java.util.List;

import static lib.FOOLlib.MEMSIZE;

public class VMCore {

    private static final int CODESIZE = 10000;
	
    private List<VMState> oldStates;
	private VMState state;

	private int[] code;
	
	private String result;
	
	public VMCore( int[] code ) {

		this.code = code;
		this.oldStates = new LinkedList<VMState>( );
		this.state = new VMState( new int[MEMSIZE], 0, MEMSIZE, 0, 0, 0, MEMSIZE );
	}

	public boolean nextStep( ) {
		if ( result == null ) {
			try {
				if ( state.getSp( ) > state.getHp( ) ) {
		
					oldStates.add( 0, state );
					state = new VMState( state );
					
					int bytecode = fetch( );
					int v1, v2;
					int address;
					switch ( bytecode ) {
						case SVMParser.PUSH:
							v1 = fetch( );
							push( v1 );
							break;
						case SVMParser.POP:
							pop( );
							break;
						case SVMParser.ADD:
							v1 = pop( );
							v2 = pop( );
							push( v2 + v1 );
							break;
						case SVMParser.SUB:
							v1 = pop( );
							v2 = pop( );
							push( v2 - v1 );
							break;
						case SVMParser.MULT:
							v1 = pop( );
							v2 = pop( );
							push( v2 * v1 );
							break;
						case SVMParser.DIV:
							v1 = pop( );
							v2 = pop( );
							push( v2 / v1 );
							break;
						case SVMParser.STOREW:
							address = pop( );
							state.getMemory( )[address] = pop( );
							break;
						case SVMParser.LOADW:
							push( state.getMemory( )[pop( )] );
							break;
						case SVMParser.BRANCH:
							state.setIp( code[state.getIp( )] );
							break;
						case SVMParser.BRANCHEQ:
							address = fetch( );
							v1 = pop( );
							v2 = pop( );
							state.setIp( v2 == v1 ? address : state.getIp( ) );
							break;
						case SVMParser.BRANCHLESSEQ:
							address = fetch( );
							v1 = pop( );
							v2 = pop( );
							state.setIp( v2 <= v1 ? address : state.getIp( ) );
							break;
						case SVMParser.JS:
							address = pop( );
							state.setRa( state.getIp( ) );
							state.setIp( address );
							break;
						case SVMParser.LOADRA:
							push( state.getRa( ) );
							break;
						case SVMParser.STORERA:
							state.setRa( pop( ) );
							break;
						case SVMParser.LOADTM:
							push( state.getTm( ) );
							break;
						case SVMParser.STORETM:
							state.setTm( pop( ) );
							break;
						case SVMParser.LOADFP:
							push( state.getFp( ) );
							break;
						case SVMParser.STOREFP:
							state.setFp( pop( ) );
							break;
						case SVMParser.COPYFP:
							state.setFp( state.getSp( ) );
							break;
						case SVMParser.LOADHP:
							push( state.getHp( ) );
							break;
						case SVMParser.STOREHP:
							state.setHp( pop( ) );
							break;
						case SVMParser.PRINT:
							result = state.getSp( ) == MEMSIZE ? "EMPTY STACK" : Integer.toString( state.getMemory( )[ state.getSp( ) ] );
							break;
						case SVMParser.HALT:
							return false;
					}
				} else {
					result = "Segmentation fault. ";
					throw new IllegalStateException( );
				}
				
				if ( state.getIp( ) < 0 || state.getIp( ) > CODESIZE ) {
					result = "Invalid value in Instruction Pointer (IP) register. ";
					throw new IllegalStateException( );
				}

				return true;
			} catch ( Exception e ) {
				result += "VM state when crash occurred was:\n" + state;
				state = oldStates.remove( 0 );
				e.printStackTrace( );
			}
		}
		return true;
	}
	
	public void backStep( ) {
		if ( oldStates.size( ) > 0 )
			state = oldStates.remove( 0 );
		result = null;
	}

	public VMState getState( ) {
		return state;
	}
	
	public String getResult( ) {
		return result;
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