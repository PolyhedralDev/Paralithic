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
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests the {@link Parser} class.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/09
 */
public class ParserTest {
    private static final Parser p;
    private static final double EPSILON = 1.0E-5;

    static {
        p = new Parser(new Parser.ParseOptions(true));
    }
    @Test
    public void simple() throws ParseException {
        assertEquals(-109d, p.parse("1 - (10 - -100)").evaluate(), EPSILON);
        assertEquals(10, p.parse("1 + 2 + 3 + 4").evaluate(), EPSILON);
        assertEquals(0.01d, p.parse("1 / 10 * 10 / 100").evaluate(), EPSILON);
        assertEquals(-89d, p.parse("1 + 10 - 100").evaluate(), EPSILON);
        assertEquals(91d, p.parse("1 - 10 - -100").evaluate(), EPSILON);
        assertEquals(91d, p.parse("1 - 10  + 100").evaluate(), EPSILON);
        assertEquals(-109d, p.parse("1 - (10 + 100)").evaluate(), EPSILON);
        assertEquals(-89d, p.parse("1 + (10 - 100)").evaluate(), EPSILON);
        assertEquals(100d, p.parse("1 / 1 * 100").evaluate(), EPSILON);
        assertEquals(0.01d, p.parse("1 / (1 * 100)").evaluate(), EPSILON);
        assertEquals(0.01d, p.parse("1 * 1 / 100").evaluate(), EPSILON);
        assertEquals(7d, p.parse("3+4").evaluate(), EPSILON);
        assertEquals(7d, p.parse("3      +    4").evaluate(), EPSILON);
        assertEquals(-1d, p.parse("3+ -4").evaluate(), EPSILON);
        assertEquals(-1d, p.parse("3+(-4)").evaluate(), EPSILON);
    }

    @Test
    public void number() throws ParseException {
        assertEquals(4003.333333d, p.parse("3.333_333+4_000").evaluate(), EPSILON);
        assertEquals(0.03, p.parse("3e-2").evaluate(), EPSILON);
        assertEquals(300d, p.parse("3e2").evaluate(), EPSILON);
        assertEquals(300d, p.parse("3e+2").evaluate(), EPSILON);
        assertEquals(320d, p.parse("3.2e2").evaluate(), EPSILON);
        assertEquals(0.032, p.parse("3.2e-2").evaluate(), EPSILON);
        assertEquals(0.03, p.parse("3E-2").evaluate(), EPSILON);
        assertEquals(300d, p.parse("3E2").evaluate(), EPSILON);
        assertEquals(300d, p.parse("3E+2").evaluate(), EPSILON);
        assertEquals(320d, p.parse("3.2E2").evaluate(), EPSILON);
        assertEquals(0.032, p.parse("3.2E-2").evaluate(), EPSILON);
    }

    @Test
    public void precedence() throws ParseException {
        // term vs. product
        assertEquals(19d, p.parse("3+4*4").evaluate(), EPSILON);
        // product vs. power
        assertEquals(20.25d, p.parse("3^4/4").evaluate(), EPSILON);
        // relation vs. product
        assertEquals(1d, p.parse("3 < 4*4").evaluate(), EPSILON);
        assertEquals(0d, p.parse("3 > 4*4").evaluate(), EPSILON);
        // brackets
        assertEquals(28d, p.parse("(3 + 4) * 4").evaluate(), EPSILON);
        assertEquals(304d, p.parse("3e2 + 4").evaluate(), EPSILON);
        assertEquals(1200d, p.parse("3e2 * 4").evaluate(), EPSILON);
    }

    @Test
    public void signed() throws ParseException {
        assertEquals(-2.02, p.parse("-2.02").evaluate(), EPSILON);
        assertEquals(2.02, p.parse("+2.02").evaluate(), EPSILON);
        assertEquals(1.01, p.parse("+2.02 + -1.01").evaluate(), EPSILON);
        assertEquals(-4.03, p.parse("-2.02 - +2.01").evaluate(), EPSILON);
        assertEquals(3.03, p.parse("+2.02 + +1.01").evaluate(), EPSILON);
    }

    @Test
    public void blockComment() throws ParseException {
        assertEquals(29, p.parse("27+ /*xxx*/ 2").evaluate(), EPSILON);
        assertEquals(29, p.parse("27+/*xxx*/ 2").evaluate(), EPSILON);
        assertEquals(29, p.parse("27/*xxx*/+2").evaluate(), EPSILON);
    }

    @Test
    public void startingWithDecimalPoint() throws ParseException {
        assertEquals(.2, p.parse(".2").evaluate(), EPSILON);
        assertEquals(.2, p.parse("+.2").evaluate(), EPSILON);
        assertEquals(.4, p.parse(".2+.2").evaluate(), EPSILON);
        assertEquals(.4, p.parse(".6+-.2").evaluate(), EPSILON);
    }

    @Test
    public void signedParentheses() throws ParseException {
        assertEquals(0.2, p.parse("-(-0.2)").evaluate(), EPSILON);
        assertEquals(1.2, p.parse("1-(-0.2)").evaluate(), EPSILON);
        assertEquals(0.8, p.parse("1+(-0.2)").evaluate(), EPSILON);
        assertEquals(2.2, p.parse("+(2.2)").evaluate(), EPSILON);
    }

    @Test
    public void trailingDecimalPoint() throws ParseException {
        assertEquals(2., p.parse("2.").evaluate(), EPSILON);
    }

    @Test
    public void signedValueAfterOperand() throws ParseException {
        assertEquals(-1.2, p.parse("1+-2.2").evaluate(), EPSILON);
        assertEquals(3.2, p.parse("1++2.2").evaluate(), EPSILON);
        assertEquals(6 * -1.1, p.parse("6*-1.1").evaluate(), EPSILON);
        assertEquals(6 * 1.1, p.parse("6*+1.1").evaluate(), EPSILON);
    }

    @Test
    public void variables() throws ParseException {
        Scope scope = new Scope();

        scope.create("a", 2);
        scope.create("b", 3);
        Expression expr = p.parse("3*a + 4 * b", scope);

        assertEquals(18d, expr.evaluate(), EPSILON);
        assertEquals(18d, expr.evaluate(), EPSILON);
    }

    @Test
    public void functions() throws ParseException {
        assertEquals(0d, p.parse("1 + sin(-pi) + cos(pi)").evaluate(), EPSILON);
        assertEquals(4.72038341576d, p.parse("tan(sqrt(euler ^ (pi * 3)))").evaluate(), EPSILON);
        assertEquals(3d, p.parse("| 3 - 6 |").evaluate(), EPSILON);
        assertEquals(3d, p.parse("if(3 > 2 && 2 < 3, 2+1, 1+1)").evaluate(), EPSILON);
        assertEquals(2d, p.parse("if(3 < 2 || 2 > 3, 2+1, 1+1)").evaluate(), EPSILON);
        assertEquals(3d, p.parse("if(1, 2+1, 1+1)").evaluate(), EPSILON);
        assertEquals(2d, p.parse("if(0, 2+1, 1+1)").evaluate(), EPSILON);
        assertEquals(2d, p.parse("min(3,2)").evaluate(), EPSILON);
        assertEquals(2d, p.parse("abs(2)").evaluate(), EPSILON);
        assertEquals(2d, p.parse("abs(-2)").evaluate(), EPSILON);
        assertEquals(-3d, p.parse("floor(-2.2)").evaluate(), EPSILON);
        assertEquals(-2d, p.parse("ceil(-2.2)").evaluate(), EPSILON);

        Scope scope = new Scope();
        scope.addInvocationVariable("x");
        assertEquals(1d, p.parse("if(x, 0, 1)", scope).evaluate(0), EPSILON);
        assertEquals(0d, p.parse("if(x, 0, 1)", scope).evaluate(10), EPSILON);


        // Test a var arg method...
        p.registerFunction("avg", avgFun);
        assertEquals(3.25d, p.parse("avg(3,2,1,7)").evaluate(), EPSILON);
    }

    DynamicFunction avgFun = new DynamicFunction() {
        @Override
        public int getArgNumber() {
            return -1;
        }

        @Override
        public double eval(double... args) {
            double avg = 0;
            if (args.length == 0) {
                return avg;
            }
            for (double e : args) {
                avg += e;
            }
            return avg / args.length;
        }

        @Override
        public @NotNull Statefulness statefulness() {
            return Statefulness.STATELESS;
        }
    };

    @Test
    public void multiInstance() throws ParseException {
        Parser p2 = new Parser();
        p2.registerFunction("avg", avgFun);
        Parser p3 = new Parser();
        p3.registerFunction("avg", avgFun);
        assertEquals(3.25d, p2.parse("avg(3,2,1,7)").evaluate(), EPSILON);
        assertEquals(4.5d, p3.parse("avg(4,5,4,5)").evaluate(), EPSILON);
    }

    @Test
    public void scopes() throws ParseException {
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

        Expression expr1 = p.parse("a + b + c + d", subScope1);
        Expression expr2 = p.parse("a + b + c + d", subScope2);
        assertEquals(15d, expr1.evaluate(), EPSILON);
        assertEquals(17d, expr2.evaluate(), EPSILON);
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
            assertEquals(a, p.parse("let a := 3 in a", root).evaluate(x), EPSILON);
        }

        { // Simple single binding to invocation variable
            double a = x;
            assertEquals(a * a, p.parse("let a := x in a * a", root).evaluate(x), EPSILON);
        }

        { // Simple single binding to arithmetic
            double a = x + x;
            assertEquals(a, p.parse("let a := x + x in a", root).evaluate(x), EPSILON);
        }

        // Empty binding
        assertEquals(x, p.parse("let in x", root).evaluate(x), EPSILON);

        { // Simple nested bindings
            double b = x;
            double a = b;
            assertEquals(a, p.parse("let a := let b := x in b in a", root).evaluate(x), EPSILON);
        }

        { // Explicitly demonstrating expression groupings
            double a = x;
            assertEquals(a + 5, p.parse("(let a := (x) in (a)) + 5", root).evaluate(x), EPSILON);
        }

        { // Trailing comma can optionally be included
            double a = x + 3;
            assertEquals(a, p.parse("let a := x + 3, in a", root).evaluate(x), EPSILON);
        }

        { // Multiple bindings
            double a = x + 3;
            double b = x + 7;
            assertEquals(a * b, p.parse("""
                    let
                        a := x + 3,
                        b := x + 7
                    in
                        a * b
                    """, root).evaluate(x), EPSILON);

            // Optional trailing comma is permitted
            assertEquals(a * b, p.parse("""
                    let
                        a := x + 3,
                        b := x + 7,
                    in
                        a * b
                    """, root).evaluate(x), EPSILON);
        }

        // Comma must delimit bindings
        assertThrows(ParseException.class, () -> p.parse("""
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
            assertEquals(a / c, p.parse("""
                    let
                        a := x + 3,
                        b := a * a,
                        c := b + a,
                    in
                        a / c
                    """, root).evaluate(x), EPSILON);
        }

        // Bindings cannot make use of bindings declared later within the same let expression
        assertThrows(ParseException.class, () -> p.parse("""
                let
                    c := b + a,
                    b := a * a,
                    a := x + 3,
                in
                    a / c
                """, root).evaluate(x));

        { // Bindings will shadow invocation variables
            double xShadowed = 10; // Must not equal x for test to be valid
            assertEquals(xShadowed, p.parse("let x := 10 in x", root).evaluate(x), EPSILON);
        }

        { // Bindings will shadow constants
            double piShadowed = 10;
            assertEquals(piShadowed, p.parse("let pi := 10 in pi", root).evaluate(x), EPSILON);
        }

        { // Nested bindings will shadow bindings made in an enclosing scope
            double aShadowed = 10;
            assertEquals(aShadowed, p.parse("""
                    let a := 5 in
                      let a := 10 in
                        a
                    """, root).evaluate(x), EPSILON);
        }

        // Should not be able to bind same name multiple times within same let expression
        assertThrows(ParseException.class, () -> p.parse("""
                let
                  a := 5,
                  a := 10
                in a
                """, root).evaluate(x));

        // Should not be able to reference name bound in child scope
        assertThrows(ParseException.class, () -> p.parse("""
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
            assertEquals(result, p.parse(expression, root).evaluate(x), EPSILON);
        }
    }

    @Test
    public void errors() {
        // We expect the parser to continue after an recoverable error!
        try {
            p.parse("test(1 2)+sin(1,2)*34-34.45.45+");
            fail();
        } catch (ParseException e) {
            assertEquals(5, e.getErrors().size());
        }

        // We expect the parser to report an invalid quantifier.
        try {
            p.parse("1x");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unfinished expression
        try {
            p.parse("1(");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            p.parse("3ee3");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            p.parse("3e3.3");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected token.
        try {
            p.parse("3e");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    public void relationalOperators() throws ParseException {
        // Test for Issue with >= and <= operators (#4)
        assertEquals(1d, p.parse("5 <= 5").evaluate(), EPSILON);
        assertEquals(1d, p.parse("5 >= 5").evaluate(), EPSILON);
        assertEquals(0d, p.parse("5 < 5").evaluate(), EPSILON);
        assertEquals(0d, p.parse("5 > 5").evaluate(), EPSILON);
    }

    @Test
    public void quantifiers() throws ParseException {
        assertEquals(1000d, p.parse("1K").evaluate(), EPSILON);
        assertEquals(1000d, p.parse("1M * 1m").evaluate(), EPSILON);
        assertEquals(1d, p.parse("1n * 1G").evaluate(), EPSILON);
        assertEquals(1d, p.parse("(1M / 1k) * 1m").evaluate(), EPSILON);
        assertEquals(1d, p.parse("1u * 10 k * 1000  m * 0.1 k").evaluate(), EPSILON);
    }

    @Test
    public void errorOnUnknownVariable() throws ParseException {
        Scope s = new Scope();
        try {
            s.create("a", 0);
            s.create("b", 0);
            p.parse("a*b+c", s);
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        s.create("c", 0);
        p.parse("a*b+c", s);
    }

    @Test
    public void removeVariable() {
        Scope s = new Scope();
        s.create("X", 0);
        assertNotNull(s.find("X"));
        assertNotNull(s.remove("X"));
        assertNull(s.find("X"));
    }

    @Test
    public void removeVariableFromSubscope() {
        Scope s = new Scope();
        Scope child = new Scope().withParent(s);
        s.create("X", 0);
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
                assertEquals(context, Expression.DEFAULT_CONTEXT);
                System.out.println(context);
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
                assertTrue(context instanceof CustomContext);
                System.out.println(((CustomContext) context).getBazinga());
                System.out.println(context);
                return DynamicFunction.super.eval(context, args);
            }
        });
        parser.parse("test()").evaluate(new CustomContext());
    }

    private static final class CustomContext implements Context {
        public String getBazinga() {
            return "bazinga";
        }
    }
}
