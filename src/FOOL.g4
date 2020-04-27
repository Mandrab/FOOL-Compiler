grammar FOOL;

@header {
package generated;
}

@lexer::members {
public int lexicalErrors=0;
}
  
/*******************************************************************************************************
 * PARSER RULES
 *******************************************************************************************************/

prog :
	(
		LET ( ( cls )+ ( dec )* | ( dec )+ ) IN exp
   	|	exp
   	) SEMIC EOF ;

cls :
	CLASS clsID = ID ( EXTENDS suID = ID )? LPAR ( field ( COMMA field )* )? RPAR    
 	CLPAR ( method )* CRPAR ;

dec :
	(	
		VAR vID = ID COLON vT = hotype ASS vE = exp
  	|	FUN fID = ID COLON fT = type LPAR ( parameter ( COMMA parameter )* )? RPAR 
        ( LET ( dec )+ IN )? fE = exp 
  	) SEMIC ;

field :
	fID = ID COLON fT = type ;

method :
	FUN mID = ID COLON mT = type LPAR ( parameter ( COMMA parameter )* )? RPAR
	( LET ( var SEMIC )+ IN )? mE = exp 
	SEMIC ;

parameter :
	pID = ID COLON pT = hotype ;
	
var :
	VAR vID = ID COLON vT = type ASS vE = exp ;

exp	:
		term
	|	l = exp PLUS r = term
	|	l = exp MINUS r = term
	|	l = exp OR r = term ;

term :
		factor
	|	l = term TIMES r = factor
	|	l = term DIV r = factor
	|	l = term AND r = factor ;

factor :
		value
	|	l = factor EQ r = value 
   	| 	l = factor GE r = value 
   	| 	l = factor LE r = value ;

value :
		
		MINUS? INTEGER															#integerValue
	| 	( TRUE | FALSE )														#booleanValue
	| 	NULL																	#nullValue
	| 	NEW ID LPAR ( exp ( COMMA exp )* )? RPAR								#newValue
	| 	IF exp THEN CLPAR exp CRPAR ELSE CLPAR exp CRPAR						#ifThenElseValue
	| 	NOT LPAR exp RPAR														#notValue
	| 	PRINT LPAR exp RPAR														#printValue
    | 	LPAR exp RPAR															#parenthesisBlockValue
    |	ID																		#idValue
	| 	ID LPAR ( exp ( COMMA exp )* )? RPAR									#functionCallValue
	|	oID = ID DOT mID = ID LPAR ( exp ( COMMA exp )* )? RPAR					#methodCallValue
	;
               
hotype : type | arrow ;

type :
		INT																		#intType
	| 	BOOL																	#boolType
 	| 	ID																		#idType
 	;
 	  
arrow : LPAR ( hotype ( COMMA hotype )* )? RPAR ARROW type ;          
		  
/*******************************************************************************************************
 * LEXER RULES
 *******************************************************************************************************/

PLUS  	: '+' ;
MINUS   : '-' ;
TIMES   : '*' ;
DIV 	: '/' ;
LPAR	: '(' ;
RPAR	: ')' ;
CLPAR	: '{' ;
CRPAR	: '}' ;
SEMIC 	: ';' ;
COLON   : ':' ; 
COMMA	: ',' ;
DOT	    : '.' ;
OR	    : '||';
AND	    : '&&';
NOT	    : '!' ;
GE	    : '>=' ;
LE	    : '<=' ;
EQ	    : '==' ;	
ASS	    : '=' ;
TRUE	: 'true' ;
FALSE	: 'false' ;
IF	    : 'if' ;
THEN	: 'then';
ELSE	: 'else' ;
PRINT	: 'print' ;
LET     : 'let' ;	
IN      : 'in' ;	
VAR     : 'var' ;
FUN	    : 'fun' ; 
CLASS	: 'class' ; 
EXTENDS : 'extends' ;	
NEW 	: 'new' ;	
NULL    : 'null' ;	  
INT	    : 'int' ;
BOOL	: 'bool' ;
ARROW   : '->' ;
INTEGER : '0' | ('1'..'9')('0'..'9')* ; 

ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;


WHITESP  : ( '\t' | ' ' | '\r' | '\n' )+    -> channel(HIDDEN) ;

COMMENT : '/*' (.)*? '*/' -> channel(HIDDEN) ;
 
ERR   	 : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN); 

