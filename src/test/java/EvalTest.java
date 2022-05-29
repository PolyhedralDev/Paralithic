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
public class EvalTest {
    private static final Parser p;
    private static final double EPSILON = 1.0E-5;

    static {
        p = new Parser();
    }
    @Test
    public void simple() throws ParseException {
        assertEquals(-109d, p.eval("1 - (10 - -100)"), EPSILON);
        assertEquals(10, p.eval("1 + 2 + 3 + 4"), EPSILON);
        assertEquals(0.01d, p.eval("1 / 10 * 10 / 100"), EPSILON);
        assertEquals(-89d, p.eval("1 + 10 - 100"), EPSILON);
        assertEquals(91d, p.eval("1 - 10 - -100"), EPSILON);
        assertEquals(91d, p.eval("1 - 10  + 100"), EPSILON);
        assertEquals(-109d, p.eval("1 - (10 + 100)"), EPSILON);
        assertEquals(-89d, p.eval("1 + (10 - 100)"), EPSILON);
        assertEquals(100d, p.eval("1 / 1 * 100"), EPSILON);
        assertEquals(0.01d, p.eval("1 / (1 * 100)"), EPSILON);
        assertEquals(0.01d, p.eval("1 * 1 / 100"), EPSILON);
        assertEquals(7d, p.eval("3+4"), EPSILON);
        assertEquals(7d, p.eval("3      +    4"), EPSILON);
        assertEquals(-1d, p.eval("3+ -4"), EPSILON);
        assertEquals(-1d, p.eval("3+(-4)"), EPSILON);
    }

    @Test
    public void number() throws ParseException {
        assertEquals(4003.333333d, p.eval("3.333_333+4_000"), EPSILON);
        assertEquals(0.03, p.eval("3e-2"), EPSILON);
        assertEquals(300d, p.eval("3e2"), EPSILON);
        assertEquals(300d, p.eval("3e+2"), EPSILON);
        assertEquals(320d, p.eval("3.2e2"), EPSILON);
        assertEquals(0.032, p.eval("3.2e-2"), EPSILON);
        assertEquals(0.03, p.eval("3E-2"), EPSILON);
        assertEquals(300d, p.eval("3E2"), EPSILON);
        assertEquals(300d, p.eval("3E+2"), EPSILON);
        assertEquals(320d, p.eval("3.2E2"), EPSILON);
        assertEquals(0.032, p.eval("3.2E-2"), EPSILON);
    }

    @Test
    public void precedence() throws ParseException {
        // term vs. product
        assertEquals(19d, p.eval("3+4*4"), EPSILON);
        // product vs. power
        assertEquals(20.25d, p.eval("3^4/4"), EPSILON);
        // relation vs. product
        assertEquals(1d, p.eval("3 < 4*4"), EPSILON);
        assertEquals(0d, p.eval("3 > 4*4"), EPSILON);
        // brackets
        assertEquals(28d, p.eval("(3 + 4) * 4"), EPSILON);
        assertEquals(304d, p.eval("3e2 + 4"), EPSILON);
        assertEquals(1200d, p.eval("3e2 * 4"), EPSILON);
    }

    @Test
    public void signed() throws ParseException {
        assertEquals(-2.02, p.eval("-2.02"), EPSILON);
        assertEquals(2.02, p.eval("+2.02"), EPSILON);
        assertEquals(1.01, p.eval("+2.02 + -1.01"), EPSILON);
        assertEquals(-4.03, p.eval("-2.02 - +2.01"), EPSILON);
        assertEquals(3.03, p.eval("+2.02 + +1.01"), EPSILON);
    }

    @Test
    public void blockComment() throws ParseException {
        assertEquals(29, p.eval("27+ /*xxx*/ 2"), EPSILON);
        assertEquals(29, p.eval("27+/*xxx*/ 2"), EPSILON);
        assertEquals(29, p.eval("27/*xxx*/+2"), EPSILON);
    }

    @Test
    public void startingWithDecimalPoint() throws ParseException {
        assertEquals(.2, p.eval(".2"), EPSILON);
        assertEquals(.2, p.eval("+.2"), EPSILON);
        assertEquals(.4, p.eval(".2+.2"), EPSILON);
        assertEquals(.4, p.eval(".6+-.2"), EPSILON);
    }

    @Test
    public void signedParentheses() throws ParseException {
        assertEquals(0.2, p.eval("-(-0.2)"), EPSILON);
        assertEquals(1.2, p.eval("1-(-0.2)"), EPSILON);
        assertEquals(0.8, p.eval("1+(-0.2)"), EPSILON);
        assertEquals(2.2, p.eval("+(2.2)"), EPSILON);
    }

    @Test
    public void trailingDecimalPoint() throws ParseException {
        assertEquals(2., p.eval("2."), EPSILON);
    }

    @Test
    public void signedValueAfterOperand() throws ParseException {
        assertEquals(-1.2, p.eval("1+-2.2"), EPSILON);
        assertEquals(3.2, p.eval("1++2.2"), EPSILON);
        assertEquals(6 * -1.1, p.eval("6*-1.1"), EPSILON);
        assertEquals(6 * 1.1, p.eval("6*+1.1"), EPSILON);
    }

    @Test
    public void variables() throws ParseException {
        Scope scope = new Scope();

        scope.create("a", 2);
        scope.create("b", 3);
        double result = p.eval("3*a + 4 * b", scope);

        assertEquals(18d, result, EPSILON);
        assertEquals(18d, result, EPSILON);
    }

    @Test
    public void functions() throws ParseException {
        assertEquals(0d, p.eval("1 + sin(-pi) + cos(pi)"), EPSILON);
        assertEquals(4.72038341576d, p.eval("tan(sqrt(euler ^ (pi * 3)))"), EPSILON);
        assertEquals(3d, p.eval("| 3 - 6 |"), EPSILON);
        assertEquals(3d, p.eval("if(3 > 2 && 2 < 3, 2+1, 1+1)"), EPSILON);
        assertEquals(2d, p.eval("if(3 < 2 || 2 > 3, 2+1, 1+1)"), EPSILON);
        assertEquals(3d, p.eval("if(1, 2+1, 1+1)"), EPSILON);
        assertEquals(2d, p.eval("if(0, 2+1, 1+1)"), EPSILON);
        assertEquals(2d, p.eval("min(3,2)"), EPSILON);
        assertEquals(2d, p.eval("abs(2)"), EPSILON);
        assertEquals(2d, p.eval("abs(-2)"), EPSILON);
        assertEquals(-3d, p.eval("floor(-2.2)"), EPSILON);
        assertEquals(-2d, p.eval("ceil(-2.2)"), EPSILON);

        Scope scope = new Scope();
        scope.addInvocationVariable("x");
        assertEquals(1d, p.eval("if(x, 0, 1)", scope, 0), EPSILON);
        assertEquals(0d, p.eval("if(x, 0, 1)", scope, 10), EPSILON);


        // Test a var arg method...
        p.registerFunction("avg", avgFun);
        assertEquals(3.25d, p.eval("avg(3,2,1,7)"), EPSILON);
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
        assertEquals(3.25d, p2.eval("avg(3,2,1,7)"), EPSILON);
        assertEquals(4.5d, p3.eval("avg(4,5,4,5)"), EPSILON);
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

        double expr1 = p.eval("a + b + c + d", subScope1);
        double expr2 = p.eval("a + b + c + d", subScope2);
        assertEquals(15d, expr1, EPSILON);
        assertEquals(17d, expr2, EPSILON);
    }

    @Test
    public void errors() {
        // We expect the parser to continue after an recoverable error!
        try {
            p.eval("test(1 2)+sin(1,2)*34-34.45.45+");
            fail();
        } catch (ParseException e) {
            assertEquals(5, e.getErrors().size());
        }

        // We expect the parser to report an invalid quantifier.
        try {
            p.eval("1x");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unfinished expression
        try {
            p.eval("1(");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            p.eval("3ee3");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            p.eval("3e3.3");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected token.
        try {
            p.eval("3e");
            fail();
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    public void relationalOperators() throws ParseException {
        // Test for Issue with >= and <= operators (#4)
        assertEquals(1d, p.eval("5 <= 5"), EPSILON);
        assertEquals(1d, p.eval("5 >= 5"), EPSILON);
        assertEquals(0d, p.eval("5 < 5"), EPSILON);
        assertEquals(0d, p.eval("5 > 5"), EPSILON);
    }

    @Test
    public void quantifiers() throws ParseException {
        assertEquals(1000d, p.eval("1K"), EPSILON);
        assertEquals(1000d, p.eval("1M * 1m"), EPSILON);
        assertEquals(1d, p.eval("1n * 1G"), EPSILON);
        assertEquals(1d, p.eval("(1M / 1k) * 1m"), EPSILON);
        assertEquals(1d, p.eval("1u * 10 k * 1000  m * 0.1 k"), EPSILON);
    }

    @Test
    public void errorOnUnknownVariable() throws ParseException {
        Scope s = new Scope();
        try {
            s.create("a", 0);
            s.create("b", 0);
            p.eval("a*b+c", s);
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        s.create("c", 0);
        p.eval("a*b+c", s);
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
        parser.eval("test()");
    }

    private static final class CustomContext implements Context {
        public String getBazinga() {
            return "bazinga";
        }
    }
}
