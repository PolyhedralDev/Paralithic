/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.parser;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.ExpressionBuilder;
import com.dfsek.paralithic.eval.ParserUtil;
import com.dfsek.paralithic.eval.tokenizer.ParseError;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import com.dfsek.paralithic.eval.tokenizer.Token;
import com.dfsek.paralithic.eval.tokenizer.Tokenizer;
import com.dfsek.paralithic.functions.Function;
import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.functions.natives.NativeFunction;
import com.dfsek.paralithic.functions.natives.NativeMath;
import com.dfsek.paralithic.functions.node.NodeFunction;
import com.dfsek.paralithic.functions.node.TernaryIfFunction;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.special.InvocationVariableNode;
import com.dfsek.paralithic.node.special.function.FunctionNode;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;
import com.dfsek.paralithic.node.unary.AbsoluteValueNode;
import com.dfsek.paralithic.node.unary.NegationNode;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Parses a given mathematical expression into an abstract syntax tree which can be evaluated.
 * <p>
 * Takes a string input as String or Reader which will be translated into an {@link Node}. If one or more errors
 * occur, a {@link ParseException} will be thrown. The parser tries to continue as long a possible to provide good
 * insight into the errors within the expression.
 * <p>
 * This is a recursive descending parser which has a method per non-terminal.
 * <p>
 * Using this parser is as easy as:
 * {@code
 * Scope scope = Scope.create();
 * NamedConstant a = scope.getVariable("a");
 * Expression expr = Parser.parse("3 + a * 4");
 * a.setValue(4);
 * System.out.println(expr.evaluate());
 * a.setValue(5);
 * System.out.println(expr.evaluate());
 * }
 */
public class Parser {

    private static final double[] D0 = new double[0];

    private final Scope scope;
    private final List<ParseError> errors = new ArrayList<>();
    private final Tokenizer tokenizer;
    private final Map<String, Function> functionTable = new TreeMap<>();

    /*
     * Setup well known functions
     */ {
        registerFunction("sin", NativeMath.SIN);
        registerFunction("cos", NativeMath.COS);
        registerFunction("tan", NativeMath.TAN);

        registerFunction("floor", NativeMath.FLOOR);
        registerFunction("ceil", NativeMath.CEIL);
        registerFunction("round", NativeMath.ROUND);

        registerFunction("pow", NativeMath.POW);

        registerFunction("min", NativeMath.MIN);
        registerFunction("max", NativeMath.MAX);

        registerFunction("sqrt", NativeMath.SQRT);

        registerFunction("sinh", NativeMath.SINH);
        registerFunction("cosh", NativeMath.COSH);
        registerFunction("tanh", NativeMath.TANH);

        registerFunction("asin", NativeMath.ASIN);
        registerFunction("acos", NativeMath.ACOS);
        registerFunction("atan", NativeMath.ATAN);
        registerFunction("atan2", NativeMath.ATAN2);

        registerFunction("rad", NativeMath.RAD);
        registerFunction("deg", NativeMath.DEG);

        registerFunction("abs", NativeMath.ABS);

        registerFunction("log", NativeMath.LOG);
        registerFunction("ln", NativeMath.LN);

        registerFunction("exp", NativeMath.EXP);

        registerFunction("sign", NativeMath.SIGN);

        registerFunction("sigmoid", NativeMath.SIGMOID);

        registerFunction("if", new TernaryIfFunction());
    }

    public Parser() {
        this(new StringReader(""), new Scope(), new TreeMap<>());
    }

    protected Parser(Reader input, Scope scope, Map<String, Function> functionTable) {
        this.scope = scope;
        tokenizer = new Tokenizer(input);
        tokenizer.setProblemCollector(errors);
        this.functionTable.putAll(functionTable);
    }

    public Scope getScope() {
        return scope;
    }

    /**
     * Registers a new function which can be referenced from within an expression.
     * <p>
     * A function must be registered before an expression is parsed in order to be visible.
     *
     * @param name     the name of the function. If a function with the same name is already available, it will be
     *                 overridden
     * @param function the function which is invoked as an expression is evaluated
     */
    public void registerFunction(String name, Function function) {
        functionTable.put(name, function);
    }


    /**
     * Parses the given input into an expression.
     *
     * @param input the expression to be parsed
     * @return the resulting AST as expression
     * @throws ParseException if the expression contains one or more errors
     */
    public Expression parse(String input) throws ParseException {
        return new Parser(new StringReader(input), new Scope(), functionTable).parse();
    }

    /**
     * Parses the given input into an expression.
     *
     * @param input the expression to be parsed
     * @return the resulting AST as expression
     * @throws ParseException if the expression contains one or more errors
     */
    public Expression parse(Reader input) throws ParseException {
        return new Parser(input, new Scope(), functionTable).parse();
    }

    /**
     * Parses the given input into an expression.
     * <p>
     * Referenced variables will be resolved using the given Scope
     *
     * @param input the expression to be parsed
     * @param scope the scope used to resolve variables
     * @return the resulting AST as expression
     * @throws ParseException if the expression contains one or more errors
     */
    public Expression parse(String input, Scope scope) throws ParseException {
        return new Parser(new StringReader(input), scope, functionTable).parse();
    }

    /**
     * Parses the given input into an expression.
     * <p>
     * Referenced variables will be resolved using the given Scope
     *
     * @param input the expression to be parsed
     * @param scope the scope used to resolve variables
     * @return the resulting AST as expression
     * @throws ParseException if the expression contains one or more errors
     */
    public Expression parse(Reader input, Scope scope) throws ParseException {
        return new Parser(input, scope, functionTable).parse();
    }

    /**
     * Parses the expression in <tt>input</tt>
     *
     * @return the parsed expression
     * @throws ParseException if the expression contains one or more errors
     */
    public Expression parse() throws ParseException {
        Node result = parseExpression();
        Map<String, DynamicFunction> dynamicFunctionMap = new TreeMap<>();
        functionTable.forEach((id, f) -> {
            if (f instanceof DynamicFunction) dynamicFunctionMap.put(id, (DynamicFunction) f);
        });
        return new ExpressionBuilder(dynamicFunctionMap).get(result);
    }

    public double eval(String expression) throws ParseException {
        return eval(expression, D0);
    }

    public double eval(String expression, double... args) throws ParseException {
        return eval(expression, new Scope(), args);
    }

    public double eval(String expression, Scope scope, double... args) throws ParseException {
        return new Parser(new StringReader(expression), scope, functionTable).eval(args);
    }

    public double eval(double... args) throws ParseException {
        return parseExpression().eval(args);
    }

    /**
     * Parses an entire expression, returning the resulting {@link Node} tree.
     *
     * @return The parsed node tree
     * @throws ParseException if the expression contains one or more errors
     */
    public Node parseExpression() throws ParseException {
        Node result = expression();
        if (tokenizer.current().isNotEnd()) {
            Token token = tokenizer.consume();
            errors.add(ParseError.error(token,
                    String.format("Unexpected token: '%s'. Expected an expression.",
                            token.getSource())));
        }
        if (!errors.isEmpty()) {
            throw ParseException.create(errors);
        }
        return result;
    }

    /**
     * Parser rule for parsing an expression.
     * <p>
     * This is the root rule. An expression is a <tt>relationalExpression</tt> which might be followed by a logical
     * operator (&amp;&amp; or ||) and another <tt>expression</tt>.
     * <p>
     * After this is invoked, {@link Parser#errors} should be checked for any errors.
     *
     * @return an expression parsed from the given input
     */
    protected Node expression() {
        Node left = relationalExpression();
        if (tokenizer.current().isSymbol("&&")) {
            tokenizer.consume();
            Node right = expression();
            return reOrder(left, right, BinaryNode.Op.AND);
        }
        if (tokenizer.current().isSymbol("||")) {
            tokenizer.consume();
            Node right = expression();
            return reOrder(left, right, BinaryNode.Op.OR);
        }
        return left;
    }

    /**
     * Returns the errors produced during the parsing of this expression.
     *
     * @return The errors produced from the parsing of this expression.
     */
    public List<ParseError> getErrors() {
        return errors;
    }


    /**
     * Parser rule for parsing a relational expression.
     * <p>
     * A relational expression is a <tt>term</tt> which might be followed by a relational operator
     * (&lt;,&lt;=,...,&gt;) and another <tt>relationalExpression</tt>.
     *
     * @return a relational expression parsed from the given input
     */
    protected Node relationalExpression() {
        Node left = term();
        if (tokenizer.current().isSymbol("<")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.LT);
        }
        if (tokenizer.current().isSymbol("<=")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.LT_EQ);
        }
        if (tokenizer.current().isSymbol("=")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.EQ);
        }
        if (tokenizer.current().isSymbol(">=")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.GT_EQ);
        }
        if (tokenizer.current().isSymbol(">")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.GT);
        }
        if (tokenizer.current().isSymbol("!=")) {
            tokenizer.consume();
            Node right = relationalExpression();
            return reOrder(left, right, BinaryNode.Op.NEQ);
        }
        return left;
    }

    /**
     * Parser rule for parsing a term.
     * <p>
     * A term is a <tt>product</tt> which might be followed by + or - as operator and another <tt>term</tt>.
     *
     * @return a term parsed from the given input
     */
    protected Node term() {
        Node left = product();
        if (tokenizer.current().isSymbol("+")) {
            tokenizer.consume();
            Node right = term();
            return reOrder(left, right, BinaryNode.Op.ADD);
        }
        if (tokenizer.current().isSymbol("-")) {
            tokenizer.consume();
            Node right = term();
            return reOrder(left, right, BinaryNode.Op.SUBTRACT);
        }
        if (tokenizer.current().isNumber()) {
            if (tokenizer.current().getContents().startsWith("-")) {
                Node right = term();
                return reOrder(left, right, BinaryNode.Op.ADD);
            }
        }

        return left;
    }

    /**
     * Parser rule for parsing a product.
     * <p>
     * A product is a <tt>power</tt> which might be followed by *, / or % as operator and another <tt>product</tt>.
     *
     * @return a product parsed from the given input
     */
    protected Node product() {
        Node left = power();
        if (tokenizer.current().isSymbol("*")) {
            tokenizer.consume();
            Node right = product();
            return reOrder(left, right, BinaryNode.Op.MULTIPLY);
        }
        if (tokenizer.current().isSymbol("/")) {
            tokenizer.consume();
            Node right = product();
            return reOrder(left, right, BinaryNode.Op.DIVIDE);
        }
        if (tokenizer.current().isSymbol("%")) {
            tokenizer.consume();
            Node right = product();
            return reOrder(left, right, BinaryNode.Op.MODULO);
        }
        return left;
    }

    /*
     * Reorders the operands of the given operation in order to generate a "left handed" AST which performs evaluations
     * in natural order (from left to right).
     */
    protected Node reOrder(Node left, Node right, BinaryNode.Op op) {
        if (right instanceof BinaryNode rightOp) {
            if (!rightOp.isSealed() && rightOp.getOp().getPriority() == op.getPriority()) {
                replaceLeft(rightOp, left, op);
                return right;
            }
        }
        return ParserUtil.createBinaryOperation(op, left, right);
    }

    protected void replaceLeft(BinaryNode target, Node newLeft, BinaryNode.Op op) {
        if (target.getLeft() instanceof BinaryNode leftOp) {
            if (!leftOp.isSealed() && leftOp.getOp().getPriority() == op.getPriority()) {
                replaceLeft(leftOp, newLeft, op);
                return;
            }
        }
        target.setLeft(ParserUtil.createBinaryOperation(op, newLeft, target.getLeft()));
    }

    /**
     * Parser rule for parsing a power.
     * <p>
     * A power is an <tt>atom</tt> which might be followed by ^ or ** as operator and another <tt>power</tt>.
     *
     * @return a power parsed from the given input
     */
    protected Node power() {
        Node left = atom();
        if (tokenizer.current().isSymbol("^") || tokenizer.current().isSymbol("**")) {
            tokenizer.consume();
            Node right = power();
            return reOrder(left, right, BinaryNode.Op.POWER);
        }
        return left;
    }

    /**
     * Parser rule for parsing an atom.
     * <p>
     * An atom is either a numeric constant, an <tt>expression</tt> in brackets, an <tt>expression</tt> surrounded by
     * | to signal the absolute function, an identifier to signal a variable reference or an identifier followed by a
     * bracket to signal a function call.
     *
     * @return an atom parsed from the given input
     */
    protected Node atom() {
        if (tokenizer.current().isSymbol("-")) {
            tokenizer.consume();
            return new NegationNode(atom());
        }
        if (tokenizer.current().isSymbol("+") && tokenizer.next().isSymbol("(")) {
            // Support for brackets with a leading + like "+(2.2)" in this case we simply ignore the
            // + sign
            tokenizer.consume();
        }
        if (tokenizer.current().isSymbol("(")) {
            tokenizer.consume();
            Node result = expression();
            if (result instanceof BinaryNode) {
                ((BinaryNode) result).seal();
            }
            expect(Token.TokenType.SYMBOL, ")");
            return result;
        }
        if (tokenizer.current().isSymbol("|")) {
            tokenizer.consume();
            Node exp = expression();
            expect(Token.TokenType.SYMBOL, "|");
            return new AbsoluteValueNode(exp);
        }
        if (tokenizer.current().isIdentifier()) {
            if (tokenizer.next().isSymbol("(")) {
                return functionCall();
            }
            Token variableName = tokenizer.consume();
            NamedConstant value = scope.find(variableName.getContents());
            int index = scope.getInvocationVarIndex(variableName.getContents());
            if (index >= 0) {
                return new InvocationVariableNode(index);
            }
            if (value == null) {
                errors.add(ParseError.error(variableName,
                        String.format("Unknown variable: '%s'", variableName.getContents())));
                return Constant.of(0);
            }

            return Constant.of(value.getValue());
        }
        return literalAtom();
    }

    /**
     * Parser rule for parsing a literal atom.
     * <p>
     * An literal atom is a numeric constant.
     *
     * @return an atom parsed from the given input
     */
    private Node literalAtom() {
        if (tokenizer.current().isSymbol("+") && tokenizer.next().isNumber()) {
            // Parse numbers with a leading + sign like +2.02 by simply ignoring the +
            tokenizer.consume();
        }
        if (tokenizer.current().isNumber()) {
            double value = Double.parseDouble(tokenizer.consume().getContents());
            if (tokenizer.current().is(Token.TokenType.ID)) {
                String quantifier = tokenizer.current().getContents().intern();
                switch (quantifier) {
                    case "n":
                        value /= 1000000000.0d;
                        tokenizer.consume();
                        break;
                    case "u":
                        value /= 1000000.0d;
                        tokenizer.consume();
                        break;
                    case "m":
                        value /= 1000.0d;
                        tokenizer.consume();
                        break;
                    case "K":
                    case "k":
                        value *= 1000.0d;
                        tokenizer.consume();
                        break;
                    case "M":
                        value *= 1000000.0d;
                        tokenizer.consume();
                        break;
                    case "G":
                        value *= 1000000000.0d;
                        tokenizer.consume();
                        break;
                    default:
                        Token token = tokenizer.consume();
                        errors.add(ParseError.error(token,
                                String.format("Unexpected token: '%s'. Expected a valid quantifier.",
                                        token.getSource())));
                        break;
                }
            }
            return Constant.of(value);
        }
        Token token = tokenizer.consume();
        errors.add(ParseError.error(token,
                String.format("Unexpected token: '%s'. Expected an expression.",
                        token.getSource())));
        return Constant.of(Double.NaN);
    }

    /**
     * Parses a function call.
     *
     * @return the function call as Expression
     */
    protected Node functionCall() {
        Token funToken = tokenizer.consume();
        Function fun = functionTable.get(funToken.getContents());

        List<Node> params = new ArrayList<>();

        tokenizer.consume();
        while (!tokenizer.current().isSymbol(")") && tokenizer.current().isNotEnd()) {
            if (!params.isEmpty()) {
                expect(Token.TokenType.SYMBOL, ",");
            }
            params.add(expression());
        }
        expect(Token.TokenType.SYMBOL, ")");

        if (fun == null) {
            errors.add(ParseError.error(funToken, String.format("Unknown function: '%s'", funToken.getContents())));
            return Constant.of(Double.NaN);
        }
        if (params.size() != fun.getArgNumber() && fun.getArgNumber() >= 0) {
            errors.add(ParseError.error(funToken,
                    String.format(
                            "Number of arguments for function '%s' do not match. Expected: %d, Found: %d",
                            funToken.getContents(),
                            fun.getArgNumber(),
                            params.size())));
            return Constant.of(Double.NaN);
        }
        if (fun instanceof DynamicFunction) return new FunctionNode(params, (DynamicFunction) fun, funToken.getContents());
        else if(fun instanceof NativeFunction) return new NativeFunctionNode((NativeFunction) fun, params);
        else if(fun instanceof NodeFunction) return ((NodeFunction) fun).createNode(params);
        errors.add(ParseError.error(funToken, String.format("Unknown function implementation: '%s", fun.getClass().getName())));
        return Constant.of(Double.NaN);
    }

    /**
     * Signals that the given token is expected.
     * <p>
     * If the current input is pointing at the specified token, it will be consumed. If not, an error will be added
     * to the error list and the input remains unchanged.
     *
     * @param type    the type of the expected token
     * @param trigger the trigger of the expected token
     */
    protected void expect(Token.TokenType type, String trigger) {
        if (tokenizer.current().matches(type, trigger)) {
            tokenizer.consume();
        } else {
            errors.add(ParseError.error(tokenizer.current(),
                    String.format("Unexpected token '%s'. Expected: '%s'",
                            tokenizer.current().getSource(),
                            trigger)));
        }
    }
}
