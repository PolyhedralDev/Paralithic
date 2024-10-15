# Paralithic

Super fast expression evaluator/parser written in Java

Paralithic is a library for parsing and evaluating mathematical expressions. It uses ASM to dynamically
generate implementations of the `Expression` interface representing the expression, with expressions converted directly
into Java bytecode for maximum performance, and maximum room for JIT optimization. Paralithic also uses several AOT
optimization techniques, such as:
* Evaluating stateless functions with constant arguments at parse time, replacing them with constants.
* Evaluating binary operations with constant operands at parse time, replacing them with constants.
* Combining constants in nested commutative binary operations.

By directly generating bytecode to be executed by the JVM, and with the above optimizations, Paralithic expressions are
able to achieve about the same speed as hard-coded ones.

## Usage

```java
Parser parser = new Parser(); // Create parser instance.
Scope scope = new Scope(); // Create variable scope. This scope can hold both constants and invocation variables.
scope.addInvocationVariable("x"); // Register variable to be used in calls to #evaluate. Values are passed in the order they are registered.
scope.create("y", 3); // Create named constant y with value 3
Expression expression = parser.parse("x * 4 + pow(2, y)", scope);
expression.evaluate(3); // 20 (3*4 + 2^3 = 20)
```


## Performance

The expression `2 + ((7-5) * (3.14159 * pow(x, (12-10))) + sin(-3.141))` was evaluated in 3 different expression libraries.

The test was run for 3 iterations of 1 second each to allow the JIT to warmup and optimize as much as possible,
the next 3 iterations of 1 second each were timed and averaged.
This was then repeated 3 times, with a new JVM each time, and the results were averaged.

The `native` and `native (simplified)` tests each tested a hard-coded method containing the expanded
and simplified expression, respectively.

```
Benchmark                                             (testExpression)  (input)  Mode  Cnt    Score    Error  Units
exp4J                2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9  232.925 ± 13.574  ns/op
native               2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9   25.687 ±  1.095  ns/op
parsii               2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9   24.371 ±  0.894  ns/op
Paralithic           2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9    3.387 ±  0.887  ns/op
native (simplified)  2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9    2.906 ±  0.126  ns/op
```

Results are from tests run on an AMD Ryzen 5 3600.

<details>
<summary>Full Benchmark Results</summary>

```
Benchmark                                                                     (testExpression)  (input)  Mode  Cnt    Score    Error  Units
PerformanceTest.exp4JPerformance                                                             2        1  avgt    9   28.913 ±  3.445  ns/op
PerformanceTest.exp4JPerformance                                                             2     1000  avgt    9   28.767 ±  2.215  ns/op
PerformanceTest.exp4JPerformance                                                             2    23422  avgt    9   26.696 ±  1.242  ns/op
PerformanceTest.exp4JPerformance                                               2 + 3.14159 * x        1  avgt    9   49.046 ±  3.008  ns/op
PerformanceTest.exp4JPerformance                                               2 + 3.14159 * x     1000  avgt    9   54.834 ± 17.179  ns/op
PerformanceTest.exp4JPerformance                                               2 + 3.14159 * x    23422  avgt    9   50.308 ±  5.501  ns/op
PerformanceTest.exp4JPerformance             2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9  232.925 ± 13.574  ns/op
PerformanceTest.exp4JPerformance             2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     1000  avgt    9  238.926 ± 23.293  ns/op
PerformanceTest.exp4JPerformance             2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))    23422  avgt    9  239.316 ± 26.309  ns/op
PerformanceTest.nativePerformance                                                            2        1  avgt    9   25.070 ±  0.453  ns/op
PerformanceTest.nativePerformance                                                            2     1000  avgt    9   25.297 ±  0.309  ns/op
PerformanceTest.nativePerformance                                                            2    23422  avgt    9   25.619 ±  1.581  ns/op
PerformanceTest.nativePerformance                                              2 + 3.14159 * x        1  avgt    9   25.227 ±  0.433  ns/op
PerformanceTest.nativePerformance                                              2 + 3.14159 * x     1000  avgt    9   25.551 ±  1.514  ns/op
PerformanceTest.nativePerformance                                              2 + 3.14159 * x    23422  avgt    9   24.988 ±  0.341  ns/op
PerformanceTest.nativePerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9   25.687 ±  1.095  ns/op
PerformanceTest.nativePerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     1000  avgt    9   25.863 ±  1.797  ns/op
PerformanceTest.nativePerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))    23422  avgt    9   24.934 ±  0.266  ns/op
PerformanceTest.nativePerformanceSimplified                                                  2        1  avgt    9    2.870 ±  0.063  ns/op
PerformanceTest.nativePerformanceSimplified                                                  2     1000  avgt    9    2.934 ±  0.238  ns/op
PerformanceTest.nativePerformanceSimplified                                                  2    23422  avgt    9    2.892 ±  0.108  ns/op
PerformanceTest.nativePerformanceSimplified                                    2 + 3.14159 * x        1  avgt    9    2.933 ±  0.126  ns/op
PerformanceTest.nativePerformanceSimplified                                    2 + 3.14159 * x     1000  avgt    9    2.880 ±  0.043  ns/op
PerformanceTest.nativePerformanceSimplified                                    2 + 3.14159 * x    23422  avgt    9    2.950 ±  0.214  ns/op
PerformanceTest.nativePerformanceSimplified  2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9    2.906 ±  0.126  ns/op
PerformanceTest.nativePerformanceSimplified  2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     1000  avgt    9    2.937 ±  0.297  ns/op
PerformanceTest.nativePerformanceSimplified  2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))    23422  avgt    9    2.900 ±  0.115  ns/op
PerformanceTest.paralithicPerformance                                                        2        1  avgt    9    6.478 ±  0.203  ns/op
PerformanceTest.paralithicPerformance                                                        2     1000  avgt    9    6.435 ±  0.221  ns/op
PerformanceTest.paralithicPerformance                                                        2    23422  avgt    9    6.503 ±  0.139  ns/op
PerformanceTest.paralithicPerformance                                          2 + 3.14159 * x        1  avgt    9    1.041 ±  0.012  ns/op
PerformanceTest.paralithicPerformance                                          2 + 3.14159 * x     1000  avgt    9    1.053 ±  0.047  ns/op
PerformanceTest.paralithicPerformance                                          2 + 3.14159 * x    23422  avgt    9    1.061 ±  0.042  ns/op
PerformanceTest.paralithicPerformance        2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9    3.387 ±  0.887  ns/op
PerformanceTest.paralithicPerformance        2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     1000  avgt    9    3.111 ±  0.150  ns/op
PerformanceTest.paralithicPerformance        2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))    23422  avgt    9    3.158 ±  0.263  ns/op
PerformanceTest.parsiiPerformance                                                            2        1  avgt    9    1.407 ±  0.079  ns/op
PerformanceTest.parsiiPerformance                                                            2     1000  avgt    9    1.401 ±  0.023  ns/op
PerformanceTest.parsiiPerformance                                                            2    23422  avgt    9    1.410 ±  0.072  ns/op
PerformanceTest.parsiiPerformance                                              2 + 3.14159 * x        1  avgt    9    5.253 ±  0.400  ns/op
PerformanceTest.parsiiPerformance                                              2 + 3.14159 * x     1000  avgt    9    5.170 ±  0.380  ns/op
PerformanceTest.parsiiPerformance                                              2 + 3.14159 * x    23422  avgt    9    5.130 ±  0.119  ns/op
PerformanceTest.parsiiPerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))        1  avgt    9   24.371 ±  0.894  ns/op
PerformanceTest.parsiiPerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     1000  avgt    9   24.020 ±  0.765  ns/op
PerformanceTest.parsiiPerformance            2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))    23422  avgt    9   24.178 ±  1.397  ns/op
```

</details>

Paralithic generated the following class from the input function
(decompiled with [Vineflower](https://vineflower.org/), a fork of FernFlower):
```java
public class ExpressionIMPL_0 implements Expression {
    public ExpressionIMPL_0() {
    }

    public double evaluate(double[] var1) {
        return 1.9994073464449005 + 6.28318 * NativeMath.intPow(var2[0], 4.0);
    }
}
```

## License

Paralithic is licensed under the [MIT License](https://github.com/PolyhedralDev/Paralithic/blob/master/LICENSE).

Paralithic is a "fork" of a [modified version](https://github.com/PolyhedralDev/parsii) of
[Parsii](https://github.com/scireum/parsii), licensed under the [MIT license](https://github.com/scireum/parsii/blob/develop/LICENSE).
