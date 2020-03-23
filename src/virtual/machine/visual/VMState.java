package virtual.machine.visual;

import static lib.FOOLlib.MEMSIZE;

public class VMState {

	private int[] memory;
	
	private int ip = 0;
	private int sp = MEMSIZE;

	private int tm;
	private int hp = 0;
	private int ra;
	private int fp = MEMSIZE;
	
	public VMState( VMState oldState ) {
		memory = new int[ MEMSIZE ];
		System.arraycopy( oldState.getMemory( ), 0, memory, 0, MEMSIZE );
		this.ip = oldState.getIp( );
		this.sp = oldState.getSp( );
		this.tm = oldState.getTm( );
		this.hp = oldState.getHp( );
		this.ra = oldState.getRa( );
		this.fp = oldState.getFp( );
	}
	
	public VMState( int[] mem, int ip, int sp, int tm, int hp, int ra, int fp ) {
		memory = new int[ MEMSIZE ];
		System.arraycopy( mem, 0, memory, 0, MEMSIZE );
		this.ip = ip;
		this.sp = sp;
		this.tm = tm;
		this.hp = hp;
		this.ra = ra;
		this.fp = fp;
	}

	public int[] getMemory( ) {
		return memory;
	}

	public void setMemory( int[] memory ) {
		this.memory = memory;
	}

	public int getIp( ) {
		return ip;
	}

	public int setIp( int ip ) {
		return this.ip = ip;
	}
	
	public int incIp( int i ) {
		return ip += i;
	}

	public int getSp( ) {
		return sp;
	}

	public int setSp( int sp ) {
		return this.sp = sp;
	}
	
	public int incSp( int i ) {
		return sp += i;
	}

	public int getTm( ) {
		return tm;
	}

	public int setTm( int tm ) {
		return this.tm = tm;
	}

	public int getHp( ) {
		return hp;
	}

	public int setHp( int hp ) {
		return this.hp = hp;
	}
	
	public int incHp( int i ) {
		return hp += i;
	}

	public int getRa( ) {
		return ra;
	}

	public int setRa( int ra ) {
		return this.ra = ra;
	}

	public int getFp( ) {
		return fp;
	}

	public int setFp( int fp ) {
		return this.fp = fp;
	}
	
	@Override
	public String toString( ) {
		return "State: \n" +
				"\tIP: " + ip +
				"\tSP: " + sp +
				"\tTM: " + tm +
				"\tHP: " + hp +
				"\tRA: " + ra +
				"\tFP: " + fp;
	}
}
