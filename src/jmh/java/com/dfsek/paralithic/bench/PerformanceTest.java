package com.dfsek.paralithic.bench;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import com.dfsek.paralithic.functions.natives.NativeMath;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.openjdk.jmh.annotations.*;
import parsii.eval.Variable;

import java.util.concurrent.TimeUnit;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@Warmup(iterations = 5, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
public class PerformanceTest {
    private static final String TEST = "(2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141)))";

    Expression expression;
    parsii.eval.Expression parsiiExpression;
    Variable x;
    net.objecthunter.exp4j.Expression expExpression;

    public PerformanceTest() {
        try {
            Parser parser = new Parser();
            Scope scope = new Scope();
            scope.addInvocationVariable("x");
            expression = parser.parse(TEST, scope);

            parsii.eval.Scope scope2 = new parsii.eval.Scope();
            x = scope2.create("x");
            parsiiExpression = parsii.eval.Parser.parse(TEST, scope2);

            expExpression = new ExpressionBuilder(TEST)
                    .variable("x")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Param({"0", "1", "1000", "23422"})
    double value;

    @Benchmark
    public double paralithicPerformance() {
        return expression.evaluate(value);
    }

    @Benchmark
    public double parsiiPerformance() {
        x.setValue(value);
        return parsiiExpression.evaluate();
    }

    @Benchmark
    public double exp4JPerformance() {
        expExpression.setVariable("x", value);
        return expExpression.evaluate();
    }

    @Benchmark
    public double nativePerformanceSimplified() {
        return evaluateNativeSimplified(value);
    }

    @Benchmark
    public double nativePerformance() {
        return evaluateNative(value);
    }

    public double evaluateNativeSimplified(double... in) {
        return 1.9994073464449005D + 6.28318D * NativeMath.intPow(in[0], 4);
    }

    public double evaluateNative(double... in) {
        return 2.0D + (7.0D + -5.0D) * 3.14159D * Math.pow(in[0], 14.0D - 10.0D) + Math.sin(-3.141D);
    }
}
