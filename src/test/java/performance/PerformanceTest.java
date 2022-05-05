package performance;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import com.dfsek.paralithic.functions.natives.NativeMath;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Test;
import parsii.eval.Variable;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {
    private static final String TEST = "(2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141)))";
    private static final int ROUNDS = 1000000;
    private static final int TESTS = 20;

    @Test
    public void performance() throws ParseException {
        System.out.println("Testing Paralithic...");
        Parser parser = new Parser();
        Scope scope = new Scope();
        scope.addInvocationVariable("x");
        Expression expression = parser.parse(TEST, scope);
        new Benchmark(expression::evaluate).bench(TESTS, ROUNDS);
    }

    @Test
    public void parsiiPerformance() throws parsii.tokenizer.ParseException {
        System.out.println("Testing parsii...");
        parsii.eval.Scope scope = new parsii.eval.Scope();
        Variable x = scope.create("x");
        parsii.eval.Expression expression = parsii.eval.Parser.parse(TEST, scope);
        new Benchmark(integer -> {
            x.setValue(integer);
            return expression.evaluate();
        }).bench(TESTS, ROUNDS);
    }

    @Test
    public void exp4JPerformance() {
        System.out.println("Testing exp4j...");
        net.objecthunter.exp4j.Expression expression = new ExpressionBuilder(TEST)
                .variable("x")
                .build();
        new Benchmark(integer -> {
            expression.setVariable("x", integer);
            return expression.evaluate();
        }).bench(TESTS, ROUNDS);
    }

    @Test
    public void nativePerformanceSimplified() {
        System.out.println("Testing native (simplified)...");
        new Benchmark(this::evaluateNativeSimplified).bench(TESTS, ROUNDS);
    }

    @Test
    public void nativePerformance() {
        System.out.println("Testing native...");
        new Benchmark(this::evaluateNative).bench(TESTS, ROUNDS);
    }

    public double evaluateNativeSimplified(double... in) {
        return 1.9994073464449005D + 6.28318D * NativeMath.intPow(in[0], 4);
    }

    public double evaluateNative(double... in) {
        return 2.0D + (7.0D + -5.0D) * 3.14159D * Math.pow(in[0], 14.0D - 10.0D) + Math.sin(-3.141D);
    }

    public interface DoubleReturnFunction {
        double eval(int in);
    }

    public static class Benchmark {
        private final DoubleReturnFunction bench;

        public Benchmark(DoubleReturnFunction bench) {
            this.bench = bench;
        }

        public void bench(int loops, int rounds) {
            double add = 0;
            List<Long> times = new ArrayList<>();
            for(int i = 0; i < loops*2; i++) {
                long start = System.nanoTime();
                for(int j = 0; j < rounds; j++) {
                    add += bench.eval(j);
                }
                long end = System.nanoTime();
                if(i > loops) times.add(end-start);
            }
            double time = times.stream().mapToDouble(Long::doubleValue).reduce(0d, Double::sum)/ times.size();
            time /= 1000000;
            System.out.println("Avg for " + loops + " iterations of " + rounds + " evaluations: " + time + "ms");
            System.out.println("Aggregate: " + add);
        }
    }
}
