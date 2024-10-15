package com.dfsek.paralithic.bench;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.functions.natives.NativeMath;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import parsii.eval.Variable;

import java.util.concurrent.TimeUnit;

@Fork(3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@Warmup(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class PerformanceTest {
    static {
        System.setProperty("paralithic.debug.dump", "true");
    }
    private Expression expression;

    private Variable parsiiVariable;

    private parsii.eval.Expression parsiiExpression;

    private net.objecthunter.exp4j.Expression expExpression;

    @Param({"1", "1000", "23422"})
    private double input;

    @Param("2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))")
    private String testExpression;

    @Setup(Level.Trial)
    public void setup() {
        try {
            { // paralithic setup
                Parser parser = new Parser();
                Scope scope = new Scope();
                scope.addInvocationVariable("x");
                this.expression = parser.parse(this.testExpression, scope);
            }
            { // parsii setup
                parsii.eval.Scope scope2 = new parsii.eval.Scope();
                this.parsiiVariable = scope2.create("x");
                this.parsiiExpression = parsii.eval.Parser.parse(this.testExpression, scope2);
            }
            { // exp4j setup
                this.expExpression = new ExpressionBuilder(this.testExpression)
                        .variable("x")
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void paralithicPerformance(Blackhole blackhole) {
        blackhole.consume(this.expression.evaluate(this.input));
    }

    @Benchmark
    public void parsiiPerformance(Blackhole blackhole) {
        this.parsiiVariable.setValue(this.input);
        blackhole.consume(this.parsiiExpression.evaluate());
    }

    @Benchmark
    public void exp4JPerformance(Blackhole blackhole) {
        this.expExpression.setVariable("x", this.input);
        blackhole.consume(this.expExpression.evaluate());
    }

    @Benchmark
    public void nativePerformanceSimplified(Blackhole blackhole) {
        blackhole.consume(evaluateNativeSimplified(this.input));
    }

    @Benchmark
    public void nativePerformance(Blackhole blackhole) {
        blackhole.consume(evaluateNative(this.input));
    }

    private static double evaluateNativeSimplified(double... in) {
        return 1.9994073464449005D + 6.28318D * NativeMath.intPow(in[0], 4);
    }

    private static double evaluateNative(double... in) {
        return 2.0D + (7.0D + -5.0D) * 3.14159D * Math.pow(in[0], 14.0D - 10.0D) + Math.sin(-3.141D);
    }
}
