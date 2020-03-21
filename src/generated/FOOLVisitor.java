// Generated from FOOL.g4 by ANTLR 4.7

package generated;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FOOLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface FOOLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link FOOLParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(FOOLParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#cls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCls(FOOLParser.ClsContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#dec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDec(FOOLParser.DecContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(FOOLParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(FOOLParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(FOOLParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(FOOLParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(FOOLParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(FOOLParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(FOOLParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integerValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerValue(FOOLParser.IntegerValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanValue(FOOLParser.BooleanValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullValue(FOOLParser.NullValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewValue(FOOLParser.NewValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifThenElseValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfThenElseValue(FOOLParser.IfThenElseValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotValue(FOOLParser.NotValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintValue(FOOLParser.PrintValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesisBlockValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesisBlockValue(FOOLParser.ParenthesisBlockValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdValue(FOOLParser.IdValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCallValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallValue(FOOLParser.FunctionCallValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code methodCallValue}
	 * labeled alternative in {@link FOOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCallValue(FOOLParser.MethodCallValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#hotype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHotype(FOOLParser.HotypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntType(FOOLParser.IntTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolType(FOOLParser.BoolTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idType}
	 * labeled alternative in {@link FOOLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdType(FOOLParser.IdTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link FOOLParser#arrow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrow(FOOLParser.ArrowContext ctx);
}