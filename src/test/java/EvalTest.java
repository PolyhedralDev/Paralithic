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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the {@link Parser} class.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/09
 */
public class EvalTest {
    private static final double EPSILON = 1.0E-5;
    private Parser parser;

    @BeforeEach
    public void setup() {
        parser = new Parser();
    }

    @Test
    public void testSimpleExpressions() throws ParseException {
        assertEquals(-109, parser.eval("1 - (10 - -100)"), EPSILON);
        assertEquals(10, parser.eval("1 + 2 + 3 + 4"), EPSILON);
        assertEquals(0.01, parser.eval("1 / 10 * 10 / 100"), EPSILON);
        assertEquals(-89, parser.eval("1 + 10 - 100"), EPSILON);
        assertEquals(91, parser.eval("1 - 10 - -100"), EPSILON);
        assertEquals(91, parser.eval("1 - 10  + 100"), EPSILON);
        assertEquals(-109, parser.eval("1 - (10 + 100)"), EPSILON);
        assertEquals(-89, parser.eval("1 + (10 - 100)"), EPSILON);
        assertEquals(100, parser.eval("1 / 1 * 100"), EPSILON);
        assertEquals(0.01, parser.eval("1 / (1 * 100)"), EPSILON);
        assertEquals(0.01, parser.eval("1 * 1 / 100"), EPSILON);
        assertEquals(7, parser.eval("3+4"), EPSILON);
        assertEquals(7, parser.eval("3      +    4"), EPSILON);
        assertEquals(-1, parser.eval("3+ -4"), EPSILON);
        assertEquals(-1, parser.eval("3+(-4)"), EPSILON);
    }

    @Test
    public void testConstantNumbers() throws ParseException {
        assertEquals(4003.333333, parser.eval("3.333_333+4_000"), EPSILON);
        assertEquals(0.03, parser.eval("3e-2"), EPSILON);
        assertEquals(300, parser.eval("3e2"), EPSILON);
        assertEquals(300, parser.eval("3e+2"), EPSILON);
        assertEquals(320, parser.eval("3.2e2"), EPSILON);
        assertEquals(0.032, parser.eval("3.2e-2"), EPSILON);
        assertEquals(0.03, parser.eval("3E-2"), EPSILON);
        assertEquals(300, parser.eval("3E2"), EPSILON);
        assertEquals(300, parser.eval("3E+2"), EPSILON);
        assertEquals(320, parser.eval("3.2E2"), EPSILON);
        assertEquals(0.032, parser.eval("3.2E-2"), EPSILON);
    }

    @Test
    public void testOperatorPrecedence() throws ParseException {
        // term vs. product
        assertEquals(19, parser.eval("3+4*4"), EPSILON);
        // product vs. power
        assertEquals(20.25, parser.eval("3^4/4"), EPSILON);
        // relation vs. product
        assertEquals(1, parser.eval("3 < 4*4"), EPSILON);
        assertEquals(0, parser.eval("3 > 4*4"), EPSILON);
        // brackets
        assertEquals(28, parser.eval("(3 + 4) * 4"), EPSILON);
        assertEquals(304, parser.eval("3e2 + 4"), EPSILON);
        assertEquals(1200, parser.eval("3e2 * 4"), EPSILON);
    }

    @Test
    public void testSingedNumbers() throws ParseException {
        assertEquals(-2.02, parser.eval("-2.02"), EPSILON);
        assertEquals(2.02, parser.eval("+2.02"), EPSILON);
        assertEquals(1.01, parser.eval("+2.02 + -1.01"), EPSILON);
        assertEquals(-4.03, parser.eval("-2.02 - +2.01"), EPSILON);
        assertEquals(3.03, parser.eval("+2.02 + +1.01"), EPSILON);
    }

    @Test
    public void testBlockComment() throws ParseException {
        assertEquals(29, parser.eval("27+ /*xxx*/ 2"), EPSILON);
        assertEquals(29, parser.eval("27+/*xxx*/ 2"), EPSILON);
        assertEquals(29, parser.eval("27/*xxx*/+2"), EPSILON);
    }

    @Test
    public void testNumberBeginningWithDecimalPoint() throws ParseException {
        assertEquals(0.2, parser.eval(".2"), EPSILON);
        assertEquals(0.2, parser.eval("+.2"), EPSILON);
        assertEquals(0.4, parser.eval(".2+.2"), EPSILON);
        assertEquals(0.4, parser.eval(".6+-.2"), EPSILON);
    }

    @Test
    public void testSignedParentheses() throws ParseException {
        assertEquals(0.2, parser.eval("-(-0.2)"), EPSILON);
        assertEquals(1.2, parser.eval("1-(-0.2)"), EPSILON);
        assertEquals(0.8, parser.eval("1+(-0.2)"), EPSILON);
        assertEquals(2.2, parser.eval("+(2.2)"), EPSILON);
    }

    @Test
    public void testTrailingDecimalPoint() throws ParseException {
        assertEquals(2.0, parser.eval("2."), EPSILON);
    }

    @Test
    public void testSignedValueAfterOperand() throws ParseException {
        assertEquals(-1.2, parser.eval("1+-2.2"), EPSILON);
        assertEquals(3.2, parser.eval("1++2.2"), EPSILON);
        assertEquals(6 * -1.1, parser.eval("6*-1.1"), EPSILON);
        assertEquals(6 * 1.1, parser.eval("6*+1.1"), EPSILON);
    }

    @Test
    public void testScopeVariables() throws ParseException {
        Scope scope = new Scope();

        scope.create("a", 2);
        scope.create("b", 3);
        double result = parser.eval("3*a + 4 * b", scope);

        assertEquals(18, result, EPSILON);
        assertEquals(18, result, EPSILON);
    }

    @Test
    public void testFunctions() throws ParseException {
        assertEquals(0, parser.eval("1 + sin(-pi) + cos(pi)"), EPSILON);
        assertEquals(4.72038341576, parser.eval("tan(sqrt(euler ^ (pi * 3)))"), EPSILON);
        assertEquals(3, parser.eval("| 3 - 6 |"), EPSILON);
        assertEquals(3, parser.eval("if(3 > 2 && 2 < 3, 2+1, 1+1)"), EPSILON);
        assertEquals(2, parser.eval("if(3 < 2 || 2 > 3, 2+1, 1+1)"), EPSILON);
        assertEquals(3, parser.eval("if(1, 2+1, 1+1)"), EPSILON);
        assertEquals(2, parser.eval("if(0, 2+1, 1+1)"), EPSILON);
        assertEquals(2, parser.eval("min(3,2)"), EPSILON);
        assertEquals(2, parser.eval("abs(2)"), EPSILON);
        assertEquals(2, parser.eval("abs(-2)"), EPSILON);
        assertEquals(-3, parser.eval("floor(-2.2)"), EPSILON);
        assertEquals(-2, parser.eval("ceil(-2.2)"), EPSILON);

        Scope scope = new Scope();
        scope.addInvocationVariable("x");
        assertEquals(1, parser.eval("if(x, 0, 1)", scope, 0), EPSILON);
        assertEquals(0, parser.eval("if(x, 0, 1)", scope, 10), EPSILON);


        // Test a var arg method...
        parser.registerFunction("avg", new DynamicAverageFunction());
        assertEquals(3.25, parser.eval("avg(3,2,1,7)"), EPSILON);
    }

    @Test
    public void testMultiInstance() throws ParseException {
        Parser p2 = new Parser();
        p2.registerFunction("avg", new DynamicAverageFunction());
        Parser p3 = new Parser();
        p3.registerFunction("avg", new DynamicAverageFunction());
        assertEquals(3.25, p2.eval("avg(3,2,1,7)"), EPSILON);
        assertEquals(4.5, p3.eval("avg(4,5,4,5)"), EPSILON);
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

        assertEquals(15, parser.eval("a + b + c + d", subScope1), EPSILON);
        assertEquals(17, parser.eval("a + b + c + d", subScope2), EPSILON);
    }

    @Test
    public void testParsingErrors() {
        // We expect the parser to continue after an recoverable error!
        try {
            parser.eval("test(1 2)+sin(1,2)*34-34.45.45+");
            fail("Evaluation should fail when an operator is missing an operand");
        } catch (ParseException e) {
            assertEquals(5, e.getErrors().size());
        }

        // We expect the parser to report an invalid quantifier.
        try {
            parser.eval("1x");
            fail("Evaluation should fail when an invalid quantifier is encountered");
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unfinished expression
        try {
            parser.eval("1(");
            fail("Evaluation should fail when braces are not closed");
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            parser.eval("3ee3");
            fail("Evaluation should fail when an unexpected separator is encountered");
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected separator.
        try {
            parser.eval("3e3.3");
            fail("Evaluation should fail when an unexpected separator is encountered");
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        // We expect the parser to report an unexpected token.
        try {
            parser.eval("3e");
            fail("Evaluation should fail when an unexpected token is encountered");
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }
    }

    @Test
    public void testRelationalOperators() throws ParseException {
        // Test for Issue with >= and <= operators (#4)
        assertEquals(1, parser.eval("5 <= 5"), EPSILON);
        assertEquals(1, parser.eval("5 >= 5"), EPSILON);
        assertEquals(0, parser.eval("5 < 5"), EPSILON);
        assertEquals(0, parser.eval("5 > 5"), EPSILON);
    }

    @Test
    public void testQuantifiers() throws ParseException {
        assertEquals(1000, parser.eval("1K"), EPSILON);
        assertEquals(1000, parser.eval("1M * 1m"), EPSILON);
        assertEquals(1, parser.eval("1n * 1G"), EPSILON);
        assertEquals(1, parser.eval("(1M / 1k) * 1m"), EPSILON);
        assertEquals(1, parser.eval("1u * 10 k * 1000  m * 0.1 k"), EPSILON);
    }

    @Test
    public void testParsingErrorOnUnknownVariable() throws ParseException {
        Scope scope = new Scope();
        try {
            scope.create("a", 0);
            scope.create("b", 0);
            parser.eval("a*b+c", scope);
        } catch (ParseException e) {
            assertEquals(1, e.getErrors().size());
        }

        scope.create("c", 0);
        parser.eval("a*b+c", scope);
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
        parser.eval("test()");
    }

    private static class DynamicAverageFunction implements DynamicFunction {
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
            if (args.length == 0) {
                return avg;
            }
            for (double e : args) {
                avg += e;
            }
            return avg / args.length;
        }
    }
}
