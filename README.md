# Paralithic

[![Build Status](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.solo-studios.ca%2Fjob%2FPolyhedralDev%2Fjob%2FParalithic%2Fjob%2Fmaster%2F&style=for-the-badge&link=https%3A%2F%2Fci.solo-studios.ca%2Fjob%2FPolyhedralDev%2Fjob%2FParalithic%2Fjob%2Fmaster%2F)](https://ci.solo-studios.ca/job/PolyhedralDev/job/Paralithic/job/master/)
[![GitHub Tag](https://img.shields.io/github/v/tag/PolyhedralDev/Paralithic?sort=semver&style=for-the-badge)](https://github.com/PolyhedralDev/Paralithic/tags)
[![Chat](https://img.shields.io/discord/715448651786485780?style=for-the-badge&color=7389D8)](https://terra.polydev.org/contact.html)

Super fast expression evaluator/parser written in Java

## About

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
scope.

addInvocationVariable("x"); // Register variable to be used in calls to #evaluate. Values are passed in the order they are registered.
scope.

create("y",3); // Create named constant y with value 3

Expression expression = parser.parse("x * 4 + pow(2, y)", scope);
expression.

evaluate(3); // 20 (3*4 + 2^3 = 20)
```

## Performance

The expression `(sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))` was evaluated in 3 different
expression libraries.

The test was run for 3 iterations of 1 second each to allow the JIT to warmup and optimize as much as possible,
the next 3 iterations of 1 second each were timed and averaged.
This was then repeated 3 times, with a new JVM each time, and the results were averaged.

The `native`, `native (simplified)`, `native (optimized)` tests each tested a hard-coded method containing the expanded, simplified, and
optimized expression, respectively.

| Benchmark           | Score                  |
|---------------------|------------------------|
| exp4J               | 332.326 ± 15.629 ns/op |
| parsii              | 97.120 ± 3.215 ns/op   |
| native              | 33.003 ± 0.699 ns/op   |
| native (simplified) | 23.084 ± 0.299 ns/op   |
| Paralithic          | 5.541 ± 0.166 ns/op    |
| native (optimized)  | 5.306 ± 0.114 ns/op    |

Results are from tests run on an Intel i7-1165G7.

<details>
<summary>Full Benchmark Results</summary>

```
Benchmark                                    (input)                                                                        (testExpression)  Mode  Cnt    Score    Error  Units
PerformanceTest.exp4JPerformance                   1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9  336.341 ± 46.461  ns/op
PerformanceTest.exp4JPerformance                1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9  321.425 ±  9.348  ns/op
PerformanceTest.exp4JPerformance               23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9  332.326 ± 15.629  ns/op
PerformanceTest.nativePerformance                  1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   33.102 ±  0.384  ns/op
PerformanceTest.nativePerformance               1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   33.286 ±  1.389  ns/op
PerformanceTest.nativePerformance              23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   33.003 ±  0.699  ns/op
PerformanceTest.nativePerformanceOptimized         1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.301 ±  0.185  ns/op
PerformanceTest.nativePerformanceOptimized      1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.257 ±  0.043  ns/op
PerformanceTest.nativePerformanceOptimized     23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.306 ±  0.114  ns/op
PerformanceTest.nativePerformanceSimplified        1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   23.112 ±  0.243  ns/op
PerformanceTest.nativePerformanceSimplified     1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   22.905 ±  0.610  ns/op
PerformanceTest.nativePerformanceSimplified    23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   23.084 ±  0.299  ns/op
PerformanceTest.paralithicPerformance              1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.442 ±  0.079  ns/op
PerformanceTest.paralithicPerformance           1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.432 ±  0.115  ns/op
PerformanceTest.paralithicPerformance          23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9    5.541 ±  0.166  ns/op
PerformanceTest.parsiiPerformance                  1  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   97.353 ±  3.488  ns/op
PerformanceTest.parsiiPerformance               1000  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   96.237 ±  2.216  ns/op
PerformanceTest.parsiiPerformance              23422  (sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141) + (0%x)) * x/3 * 3/sqrt(x))  avgt    9   97.120 ±  3.215  ns/op
```

</details>

Paralithic generated the following class from the input function
(decompiled with [Vineflower](https://vineflower.org/), a fork of FernFlower):

```java
public class ExpressionIMPL_0 implements Expression {
    public ExpressionIMPL_0() {
    }

    public double evaluate(Context var1, double[] var2) {
        return TrigonometryFunctions.sin(var2[0]) + 2.0 + Math.fma(6.28318, IntegerFunctions.iPow(var2[0], 4.0), -7.669050828553736E-4);
    }
}
```

## License

Paralithic is licensed under the [MIT License](https://github.com/PolyhedralDev/Paralithic/blob/master/LICENSE).

Paralithic is a "fork" of a [modified version](https://github.com/PolyhedralDev/parsii) of
[Parsii](https://github.com/scireum/parsii), licensed under the [MIT license](https://github.com/scireum/parsii/blob/develop/LICENSE).
