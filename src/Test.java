import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.antlr.v4.runtime.*;

import ast.Node;

public class Test {
    public static void main(String[] args) throws Exception {
      
        String fileName = "fool_files" + File.separator + "quicksort.fool";
      
        CharStream chars = CharStreams.fromFileName(fileName);
        FOOLLexer lexer = new FOOLLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        //SIMPLISTIC BUT WRONG CHECK OF THE LEXER ERRORS
        if(lexer.lexicalErrors > 0){
        	System.out.println("The program was not in the right format. Exiting the compilation process now");
        }else{
        
	        FOOLParser parser = new FOOLParser(tokens);

	        ParserVisitor visitor = new ParserVisitor();
	        
	        Node ast = visitor.visit(parser.prog()); //generazione AST	        
	        	
	
	        System.out.println("Visualizing AST...");
	        System.out.println(ast.toPrint(""));
	
	        Node type = ast.typeCheck(); //type-checking bottom-up 
	        System.out.println(type.toPrint("Type checking ok! Type of the program is: "));
	        
	      
	        // CODE GENERATION  prova.fool.asm
	        String code=ast.codeGeneration(); 
	        BufferedWriter out = new BufferedWriter(new FileWriter(fileName+".asm")); 
	        out.write(code);
	        out.close(); 
	        System.out.println("Code generated! Assembling and running generated code.");
	        
	        FileInputStream isASM = new FileInputStream(fileName+".asm");
	        ANTLRInputStream inputASM = new ANTLRInputStream(isASM);
	        SVMLexer lexerASM = new SVMLexer(inputASM);
	        CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
	        SVMParser parserASM = new SVMParser(tokensASM);
	        
	        parserASM.assembly();
	        
	        System.out.println("You had: "+lexerASM.lexicalErrors+" lexical errors and "+parserASM.getNumberOfSyntaxErrors()+" syntax errors.");
	        if (lexerASM.lexicalErrors>0 || parserASM.getNumberOfSyntaxErrors()>0) System.exit(1);
	
	        System.out.println("Starting Virtual Machine...");
	        ExecuteVM vm = new ExecuteVM(parserASM.code);
	        vm.cpu();
        }
       
        
    }
}
