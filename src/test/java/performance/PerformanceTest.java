package performance;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Test;
import parsii.eval.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PerformanceTest {
    private static final String TEST = "(2 + ((7-5) * (3.14159 * x^(12-10)) + sin(-3.141)))";

    @Test
    public void performance() throws ParseException {
        System.out.println("Testing Paralithic...");
        Parser parser = new Parser();
        Scope scope = new Scope();
        scope.addInvocationVariable("x");
        Expression expression = parser.parse(TEST, scope);
        new Benchmark(expression::evaluate).bench(20, 1000000);
    }

    @Test
    public void parsiiPerformance() throws parsii.tokenizer.ParseException {
        System.out.println("Testing parsii...");
        parsii.eval.Parser parser = new parsii.eval.Parser();
        parsii.eval.Scope scope = new parsii.eval.Scope();
        Variable x = scope.create("x");
        parsii.eval.Expression expression = parser.parse(TEST, scope);
        new Benchmark(integer -> {
            x.setValue(integer);
            return expression.evaluate();
        }).bench(20, 1000000);
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
        }).bench(20, 1000000);
    }

    public static class Benchmark {
        private final Function<Integer, Double> bench;

        public Benchmark(Function<Integer, Double> bench) {
            this.bench = bench;
        }

        public void bench(int loops, int rounds) {
            double add = 0;
            List<Long> times = new ArrayList<>();
            for(int i = 0; i < loops*2; i++) {
                long start = System.nanoTime();
                for(int j = 0; j < rounds; j++) {
                    add += bench.apply(j);
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
