# FOOL Project

This project started with the aim of better understand how programming languages and compilers work. It has been made after following the *Languages, Compilers and Computational Models* course at UniBo.

## A Functional and Object Oriented Language

The project started with the design of a simple language that mixes Functional and Object Oriented paradigms (a.k.a. FOOL, with L standing for Language). Two short codes example are available below. The first one shows an higher-order use, the second one shows use of objects.

Higher-order code:

    let
        fun g:int ( x:(int, int)->int )
            x( 5, 7 );

        fun f:int ( c:int )
            let
                fun linsum:int ( a:int, b:int )
                    (a + b) * c;
            in 
                g( linsum );   
    in
        print( f( 2 ) );

Object-oriented code:
    
    let
        class Account ( money:int ) {
            fun getMon:int ( ) money;
        }
  
        class TradingAcc extends Account ( invested:int ) {
            fun getInv:int ( ) invested;
        }

        class BankLoan ( loan:Account ) {
            fun getLoan:Account ( ) loan;
            fun openLoan:Account ( m:TradingAcc )
                if ( ( m.getMon( ) + m.getInv( ) ) >= 30000 ) 
                then {
                    new Account( loan.getMon( ) )
                } else {
                    null
                };
        } 

        class MyBankLoan extends BankLoan ( loan:TradingAcc ) {
            fun openLoan:TradingAcc ( l:Account )
                if ( l.getMon( ) >= 20000 ) 
                then {
                    new TradingAcc( loan.getMon( ), loan.getInv( ) )
                } else {
                    null
                };
        }

        var bl:BankLoan = new MyBankLoan( new TradingAcc( 50000, 40000 ) );
        var myTradingAcc:TradingAcc = new TradingAcc( 20000, 5000 );
        var myLoan:Account = bl.openLoan( myTradingAcc );

    in
        print( if ( myLoan == null ) then { 0 } else { myLoan.getMon( ) } );

However, being that a learning project, FOOL is not intended to be used as a real language. That's observable in some characteristics:

* only a code file available (no imports)
* class declarations as first elements of the file
* no *class* variables (variables exist outside the class or in methods/functions!)
* immutable *class* parameters
* *class* parameters can't be lambdas (but methods and functions parameters can!)
* no deallocation of objects
* no file descriptors support

## The Compiler

It has been built in Java through the use of **ANTLR** tool. It's composed by *Lexer*, *Parser*, *Type-Checker* and *Code-Generator*. No optimization is made on the code.</br>
All these components are implemented using the *Visitor pattern* to divide the tasks in the best possible way.

### Lexer
This component is automatically builded by the ANTLR tool based on the .g4 file. In this one, the keywords of all the tokens are described.

### Parser
ANTLR auto-build a class that allow to parse the code *top-down*. This one is constructed through the grammar rules defined in the .g4 file. The rules to create the *Abstract Syntax Tree* (a "condensed" version of the *Parse Tree*) are instead defined in the *ParserVisitor* file.

### Type-Checker
This is the component that checks that the code is correctly typed (some check are indeed also done by the parser component). 

    This section has to be expanded
    
### Code-Generator

    This section has to be expanded

## Credits
**Mario Bravetti**, teacher of the course. He established the working path.</br>
**Marco Meluzzi**, **Matteo Scucchia** and **Francesca Tonetti** with whom I have implemented a different version of the compiler that does *not* use the visitor pattern.</br>
**Ylenia Battistini**, **Sophia Fantoni** and **Enrico Gnagnarella** with which various issues were discussed.
