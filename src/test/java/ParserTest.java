/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import com.dfsek.paralithic.functions.dynamic.Context;
import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.seismic.math.floatingpoint.FloatingPointConstants;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the {@link Parser} class.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/09
 */
public class ParserTest {
    private Parser parser;
    private Scope singleVariableScope;

    @BeforeEach
    public void setup() {
        parser = new Parser(new Parser.ParseOptions(true));
        singleVariableScope = new Scope();
        singleVariableScope.addInvocationVariable("x"); // we need this to avoid constant folding for some ops.
    }

    @Test
    public void testOptimisations() throws ParseException {
        // we need a scope to avoid constant folding for some ops.
        assertEquals(2, parser.parse("pow(x, 0.5)", singleVariableScope).evaluate(4), FloatingPointConstants.EPSILON);
        assertEquals(4, parser.parse("pow(x, 2)", singleVariableScope).evaluate(2), FloatingPointConstants.EPSILON);
        assertEquals(8, parser.parse("pow(x, 3)", singleVariableScope).evaluate(2), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("pow(x, 0)", singleVariableScope).evaluate(20), FloatingPointConstants.EPSILON);
        assertEquals(0.5, parser.parse("pow(x, -1)", singleVariableScope).evaluate(2), FloatingPointConstants.EPSILON);
        assertEquals(0, parser.parse("pow(0, x)", singleVariableScope).evaluate(20), FloatingPointConstants.EPSILON);
    }


    @Test
    public void testSimpleExpressions() throws ParseException {
        assertEquals(-109, parser.parse("1 - (10 - -100)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(10, parser.parse("1 + 2 + 3 + 4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.01, parser.parse("1 / 10 * 10 / 100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-89, parser.parse("1 + 10 - 100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(91, parser.parse("1 - 10 - -100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(91, parser.parse("1 - 10  + 100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-109, parser.parse("1 - (10 + 100)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-89, parser.parse("1 + (10 - 100)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(100, parser.parse("1 / 1 * 100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.01, parser.parse("1 / (1 * 100)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.01, parser.parse("1 * 1 / 100").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(7, parser.parse("3+4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(7, parser.parse("3      +    4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-1, parser.parse("3+ -4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-1, parser.parse("3+(-4)").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testConstantNumbers() throws ParseException {
        assertEquals(4003.333333, parser.parse("3.333_333+4_000").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.03, parser.parse("3e-2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(300, parser.parse("3e2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(300, parser.parse("3e+2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(320, parser.parse("3.2e2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.032, parser.parse("3.2e-2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.03, parser.parse("3E-2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(300, parser.parse("3E2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(300, parser.parse("3E+2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(320, parser.parse("3.2E2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.032, parser.parse("3.2E-2").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testOperatorPrecedence() throws ParseException {
        // term vs. product
        assertEquals(19, parser.parse("3+4*4").evaluate(), FloatingPointConstants.EPSILON);
        // product vs. power
        assertEquals(20.25, parser.parse("3^4/4").evaluate(), FloatingPointConstants.EPSILON);
        // relation vs. product
        assertEquals(1, parser.parse("3 < 4*4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0, parser.parse("3 > 4*4").evaluate(), FloatingPointConstants.EPSILON);
        // brackets
        assertEquals(28, parser.parse("(3 + 4) * 4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(304, parser.parse("3e2 + 4").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1200, parser.parse("3e2 * 4").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testSingedNumbers() throws ParseException {
        assertEquals(-2.02, parser.parse("-2.02").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2.02, parser.parse("+2.02").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1.01, parser.parse("+2.02 + -1.01").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-4.03, parser.parse("-2.02 - +2.01").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(3.03, parser.parse("+2.02 + +1.01").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testBlockComment() throws ParseException {
        assertEquals(29, parser.parse("27+ /*xxx*/ 2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(29, parser.parse("27+/*xxx*/ 2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(29, parser.parse("27/*xxx*/+2").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testNumberBeginningWithDecimalPoint() throws ParseException {
        assertEquals(0.2, parser.parse(".2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.2, parser.parse("+.2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.4, parser.parse(".2+.2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.4, parser.parse(".6+-.2").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testSignedParentheses() throws ParseException {
        assertEquals(0.2, parser.parse("-(-0.2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1.2, parser.parse("1-(-0.2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0.8, parser.parse("1+(-0.2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2.2, parser.parse("+(2.2)").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testTrailingDecimalPoint() throws ParseException {
        assertEquals(2.0, parser.parse("2.").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testSignedValueAfterOperand() throws ParseException {
        assertEquals(-1.2, parser.parse("1+-2.2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(3.2, parser.parse("1++2.2").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(6 * -1.1, parser.parse("6*-1.1").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(6 * 1.1, parser.parse("6*+1.1").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testScopeVariables() throws ParseException {
        Scope scope = new Scope();

        scope.create("a", 2);
        scope.create("b", 3);
        Expression expr = parser.parse("3*a + 4 * b", scope);

        assertEquals(18, expr.evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(18, expr.evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testFunctions() throws ParseException {
        assertEquals(0, parser.parse("1 + sin(-pi) + cos(pi)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(4.719162764826675, parser.parse("tan(sqrt(euler ^ (pi * 3)))").evaluate(), 0.001);
        assertEquals(3, parser.parse("| 3 - 6 |").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(3, parser.parse("if(3 > 2 && 2 < 3, 2+1, 1+1)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2, parser.parse("if(3 < 2 || 2 > 3, 2+1, 1+1)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(3, parser.parse("if(1, 2+1, 1+1)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2, parser.parse("if(0, 2+1, 1+1)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2, parser.parse("min(3,2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2, parser.parse("abs(2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(2, parser.parse("abs(-2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-3, parser.parse("floor(-2.2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(-2, parser.parse("ceil(-2.2)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("if(x, 0, 1)", singleVariableScope).evaluate(0), FloatingPointConstants.EPSILON);
        assertEquals(0, parser.parse("if(x, 0, 1)", singleVariableScope).evaluate(10), FloatingPointConstants.EPSILON);


        // Test a var arg method...
        parser.registerFunction("avg", new DynamicAverageFunction());
        assertEquals(3.25, parser.parse("avg(3,2,1,7)").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testMultiInstance() throws ParseException {
        Parser p2 = new Parser();
        p2.registerFunction("avg", new DynamicAverageFunction());
        Parser p3 = new Parser();
        p3.registerFunction("avg", new DynamicAverageFunction());
        assertEquals(3.25, p2.parse("avg(3,2,1,7)").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(4.5, p3.parse("avg(4,5,4,5)").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testScopes() throws ParseException {
        Scope root = new Scope();
        root.create("a", 1);
        Scope subScope1 = new Scope().withParent(root);
        Scope subScope2 = new Scope().withParent(root);
        subScope1.create("b", 2);
        subScope2.create("b", 3);
        root.create("c", 4);
        subScope1.create("c", 5);

        root.create("d", 9);
        subScope1.create("d", 7);

        assertEquals(15, parser.parse("a + b + c + d", subScope1).evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(17, parser.parse("a + b + c + d", subScope2).evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testLetBinding() throws ParseException {
        Scope root = new Scope();
        root.addInvocationVariable("x");
        // let bindings should only create new local variables in a new inner scope
        // N.B. this will not mutate / modify the outer / root scope, so it should
        // be fine to re-use the same scope instance across multiple expressions.

        double x = 5; // Arbitrary number

        { // Simple single binding to constant
            double a = 3;
            assertEquals(a, parser.parse("let a := 3 in a", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Simple single binding to invocation variable
            double a = x;
            assertEquals(a * a, parser.parse("let a := x in a * a", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Simple single binding to arithmetic
            double a = x + x;
            assertEquals(a, parser.parse("let a := x + x in a", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        // Empty binding
        assertEquals(x, parser.parse("let in x", root).evaluate(x), FloatingPointConstants.EPSILON);

        { // Simple nested bindings
            double b = x;
            double a = b;
            assertEquals(a, parser.parse("let a := let b := x in b in a", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Explicitly demonstrating expression groupings
            double a = x;
            assertEquals(a + 5, parser.parse("(let a := (x) in (a)) + 5", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Trailing comma can optionally be included
            double a = x + 3;
            assertEquals(a, parser.parse("let a := x + 3, in a", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Multiple bindings
            double a = x + 3;
            double b = x + 7;
            assertEquals(a * b, parser.parse("""
                                             let
                                                 a := x + 3,
                                                 b := x + 7
                                             in
                                                 a * b
                                             """, root).evaluate(x), FloatingPointConstants.EPSILON);

            // Optional trailing comma is permitted
            assertEquals(a * b, parser.parse("""
                                             let
                                                 a := x + 3,
                                                 b := x + 7,
                                             in
                                                 a * b
                                             """, root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        // Comma must delimit bindings
        assertThrows(ParseException.class, () -> parser.parse("""
                                                              let
                                                                  a := x + 3
                                                                  b := x + 7
                                                              in
                                                                  a * b
                                                              """, root).evaluate(x));


        { // Bindings can make use of previous bindings within the same let expression
            double a = x + 3;
            double b = a * a;
            double c = b + a;
            assertEquals(a / c, parser.parse("""
                                             let
                                                 a := x + 3,
                                                 b := a * a,
                                                 c := b + a,
                                             in
                                                 a / c
                                             """, root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        // Bindings cannot make use of bindings declared later within the same let expression
        assertThrows(ParseException.class, () -> parser.parse("""
                                                              let
                                                                  c := b + a,
                                                                  b := a * a,
                                                                  a := x + 3,
                                                              in
                                                                  a / c
                                                              """, root).evaluate(x));

        { // Bindings will shadow invocation variables
            double xShadowed = 10; // Must not equal x for test to be valid
            assertEquals(xShadowed, parser.parse("let x := 10 in x", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Bindings will shadow constants
            double piShadowed = 10;
            assertEquals(piShadowed, parser.parse("let pi := 10 in pi", root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        { // Nested bindings will shadow bindings made in an enclosing scope
            double aShadowed = 10;
            assertEquals(aShadowed, parser.parse("""
                                                 let a := 5 in
                                                   let a := 10 in
                                                     a
                                                 """, root).evaluate(x), FloatingPointConstants.EPSILON);
        }

        // Should not be able to bind same name multiple times within same let expression
        assertThrows(ParseException.class, () -> parser.parse("""
                                                              let
                                                                a := 5,
                                                                a := 10
                                                              in a
                                                              """, root).evaluate(x));

        // Should not be able to reference name bound in child scope
        assertThrows(ParseException.class, () -> parser.parse("""
                                                              (let a := x in a) + a
                                                              //             ^    ^
                                                              //             |    L_ This should cause an error as 'a' should only be scoped within the parenthesis
                                                              //             |
                                                              //             L_ This should be fine
                                                              """, root).evaluate(x));

        { // Complex nested let expressions
            double b = x * x;
            double c = b + 2;
            double f = 1337 / x;
            double d = f + f;
            double a = b + c + d;
            double result = a / x;
            String expression = """
                                let
                                  a := let
                                    b := x * x,
                                    c := b + 2,
                                    d := let f := 1337 / x in f + f
                                  in b + c + d
                                in a / x
                                """;
            assertEquals(result, parser.parse(expression, root).evaluate(x), FloatingPointConstants.EPSILON);
        }
    }

    @Test
    public void testParsingErrors() {
        // We expect the parser to continue after an recoverable error!
        try {
            parser.parse("test(1 2)+sin(1,2)*34-34.45.45+");
            fail("Evaluation should fail when an operator is missing an operand");
        } catch(ParseException e) {
            assertEquals(5, e.getErrors().size());
        }

        // We expect the parser to report an invalid quantifier.
        try {
            parser.parse("1x");
            fail("Evaluation should fail when an invalid quantifier is encountered");
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unfinished expression
        try {
            parser.parse("1(");
            fail("Evaluation should fail when braces are not closed");
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            parser.parse("3ee3");
            fail("Evaluation should fail when an unexpected separator is encountered");
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            parser.parse("3e3.3");
            fail("Evaluation should fail when an unexpected separator is encountered");
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected token.
        try {
            parser.parse("3e");
            fail("Evaluation should fail when an unexpected token is encountered");
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    public void testRelationalOperators() throws ParseException {
        // Test for Issue with >= and <= operators (#4)
        assertEquals(1, parser.parse("5 <= 5").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("5 >= 5").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0, parser.parse("5 < 5").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(0, parser.parse("5 > 5").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testQuantifiers() throws ParseException {
        assertEquals(1000, parser.parse("1K").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1000, parser.parse("1M * 1m").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("1n * 1G").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("(1M / 1k) * 1m").evaluate(), FloatingPointConstants.EPSILON);
        assertEquals(1, parser.parse("1u * 10 k * 1000  m * 0.1 k").evaluate(), FloatingPointConstants.EPSILON);
    }

    @Test
    public void testParsingErrorOnUnknownVariable() throws ParseException {
        Scope scope = new Scope();
        try {
            scope.create("a", 0);
            scope.create("b", 0);
            parser.parse("a*b+c", scope);
        } catch(ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        scope.create("c", 0);
        parser.parse("a*b+c", scope);
    }

    @Test
    public void testRemoveScopeVariable() {
        Scope scope = new Scope();
        scope.create("X", 0);
        assertNotNull(scope.find("X"));
        assertNotNull(scope.remove("X"));
        assertNull(scope.find("X"));
    }

    @Test
    public void testRemoveVariableFromSubscope() {
        Scope scope = new Scope();
        Scope child = new Scope().withParent(scope);
        scope.create("X", 0);
        assertNotNull(child.find("X"));
        assertNull(child.remove("X"));
        assertNotNull(child.find("X"));
    }

    @Test
    public void testDefaultContext() throws ParseException {
        Parser parser = new Parser();
        parser.registerFunction("test", new DynamicFunction() {
            @Override
            public int getArgNumber() {
                return 0;
            }

            @NotNull
            @Override
            public Statefulness statefulness() {
                return Statefulness.STATELESS;
            }

            @Override
            public double eval(double... args) {
                return 0;
            }

            @Override
            public double eval(Context context, double... args) {
                assertEquals(Expression.DEFAULT_CONTEXT, context);
                return DynamicFunction.super.eval(context, args);
            }
        });
        parser.parse("test()").evaluate();
    }

    @Test
    public void testCustomContext() throws ParseException {
        Parser parser = new Parser();
        parser.registerFunction("test", new DynamicFunction() {
            @Override
            public int getArgNumber() {
                return 0;
            }

            @Override
            public @NotNull Statefulness statefulness() {
                return Statefulness.STATELESS;
            }

            @Override
            public double eval(double... args) {
                return 0;
            }

            @Override
            public double eval(Context context, double... args) {
                assertInstanceOf(CustomContext.class, context);
                assertDoesNotThrow(() -> assertEquals("bazinga", ((CustomContext) context).getBazinga()));
                return DynamicFunction.super.eval(context, args);
            }
        });
        assertEquals(0, parser.parse("test()").evaluate(new CustomContext()));
    }

    private static final class CustomContext implements Context {
        public String getBazinga() {
            return "bazinga";
        }
    }


    private static final class DynamicAverageFunction implements DynamicFunction {
        @Override
        public int getArgNumber() {
            return -1;
        }

        @NotNull
        @Override
        public Statefulness statefulness() {
            return Statefulness.STATELESS;
        }

        @Override
        public double eval(double... args) {
            double avg = 0;
            if(args.length == 0) {
                return avg;
            }
            for(double e : args) {
                avg += e;
            }
            return avg / args.length;
        }
    }
}
