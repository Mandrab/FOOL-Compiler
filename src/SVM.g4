grammar SVM;

@header {

package generated;

import java.util.*;
import virtual.machine.ExecuteVM;
}

@lexer::members {

public int lexicalErrors = 0;
}

@parser::members {

public int[] code = new int[ ExecuteVM.CODESIZE ];
private int i = 0;
private Map<String,Integer> labelDef = new HashMap<String,Integer>( );
private Map<Integer,String> labelRef = new HashMap<Integer,String>( );
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

assembly: 
	(
		PUSH n = NUMBER			{code[i++] = PUSH; 
									code[i++] = Integer.parseInt($n.text);}
	|	PUSH l = LABEL			{code[i++] = PUSH; 
									labelRef.put(i++,$l.text);}
	|	POP						{code[i++] = POP;}	
	|	ADD						{code[i++] = ADD;}
	|	SUB						{code[i++] = SUB;}
	|	MULT					{code[i++] = MULT;}
	|	DIV						{code[i++] = DIV;}
	|	STOREW					{code[i++] = STOREW;}
	|	LOADW					{code[i++] = LOADW;}
	|	l = LABEL COL			{labelDef.put($l.text,i);}
	|	BRANCH l = LABEL		{code[i++] = BRANCH;
									labelRef.put(i++,$l.text);}
	|	BRANCHEQ l = LABEL		{code[i++] = BRANCHEQ;
									labelRef.put(i++,$l.text);}
	|	BRANCHLESSEQ l = LABEL	{code[i++] = BRANCHLESSEQ;
									labelRef.put(i++,$l.text);}
	|	JS						{code[i++] = JS;}
	|	LOADRA					{code[i++] = LOADRA;}
	|	STORERA					{code[i++] = STORERA;}
	|	LOADTM					{code[i++] = LOADTM;}
	|	STORETM					{code[i++] = STORETM;}
	|	LOADFP					{code[i++] = LOADFP;}
	|	STOREFP					{code[i++] = STOREFP;}
	|	COPYFP					{code[i++] = COPYFP;}
	|	LOADHP					{code[i++] = LOADHP;}
	|	STOREHP					{code[i++] = STOREHP;}
	|	PRINT					{code[i++] = PRINT;}
	|	HALT					{code[i++] = HALT;}
	)* {
		for ( Integer i : labelRef.keySet( ) )
			code[i] = labelDef.get( labelRef.get( i ) );
	} ;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

PUSH		: 'push' ;
POP			: 'pop' ;
ADD			: 'add' ;
SUB			: 'sub' ;
MULT		: 'mult' ;
DIV			: 'div' ;
STOREW		: 'sw' ;
LOADW		: 'lw' ;
BRANCH		: 'b' ;
BRANCHEQ	: 'beq' ;
BRANCHLESSEQ: 'bleq' ;
JS			: 'js' ;
LOADRA		: 'lra' ;
STORERA		: 'sra' ;
LOADTM		: 'ltm' ;
STORETM		: 'stm' ;
LOADFP		: 'lfp' ;
STOREFP		: 'sfp' ;
COPYFP		: 'cfp' ;
LOADHP		: 'lhp' ;
STOREHP		: 'shp' ;
PRINT		: 'print' ;
HALT		: 'halt' ;

COL			: ':' ;
LABEL		: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;
NUMBER		: '0' | ('-')?(('1'..'9')('0'..'9')*) ;

WHITESP		: (' '|'\t'|'\n'|'\r')+ -> channel(HIDDEN) ;

ERR			: . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN);
