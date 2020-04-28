package visitors;

import ast.AndNode;
import ast.ArrowTypeNode;
import ast.BoolNode;
import ast.BoolTypeNode;
import ast.CallNode;
import ast.ClassCallNode;
import ast.ClassNode;
import ast.ClassTypeNode;
import ast.DivNode;
import ast.EmptyNode;
import ast.EmptyTypeNode;
import ast.EqualNode;
import ast.FieldNode;
import ast.FunNode;
import ast.GreaterEqualNode;
import ast.IdNode;
import ast.IfNode;
import ast.IntNode;
import ast.IntTypeNode;
import ast.LessEqualNode;
import ast.MethodNode;
import ast.MinusNode;
import ast.NewNode;
import ast.Node;
import ast.NotNode;
import ast.OrNode;
import ast.ParNode;
import ast.PlusNode;
import ast.PrintNode;
import ast.ProgLetInNode;
import ast.ProgNode;
import ast.RefTypeNode;
import ast.STEntry;
import ast.TimesNode;
import ast.VarNode;

/**
 * Interface for an AST visitor
 * 
 * @author Paolo Baldini
 * 
 * @param <R>
 * 		expected return type of 'visit' method
 */
public interface NodeVisitor<R> {

	R visit( Node visitable );

	R visit( AndNode visitable );

	R visit( ArrowTypeNode visitable );

	R visit( BoolNode visitable );

	R visit( BoolTypeNode visitable );

	R visit( CallNode visitable );

	R visit( ClassCallNode visitable );

	R visit( ClassNode visitable );

	R visit( ClassTypeNode visitable );

	R visit( DivNode visitable );

	R visit( EmptyNode visitable );

	R visit( EmptyTypeNode visitable );

	R visit( EqualNode visitable );

	R visit( FieldNode visitable );

	R visit( FunNode visitable );

	R visit( GreaterEqualNode visitable );

	R visit( IdNode visitable );

	R visit( IfNode visitable );

	R visit( IntNode visitable );

	R visit( IntTypeNode visitable );

	R visit( LessEqualNode visitable );

	R visit( MethodNode visitable );

	R visit( MinusNode visitable );

	R visit( NewNode visitable );

	R visit( NotNode visitable );

	R visit( OrNode visitable );

	R visit( ParNode visitable );

	R visit( PlusNode visitable );

	R visit( PrintNode visitable );

	R visit( ProgLetInNode visitable );

	R visit( ProgNode visitable );

	R visit( RefTypeNode visitable );

	R visit( STEntry visitable );

	R visit( TimesNode visitable );

	R visit( VarNode visitable );

}
