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

The expression `2 + ((7-5) * (3.14159 * pow(x, (12-10))) + sin(-3.141))` was evaluated in 3 different expression libraries.

The test was run for 3 iterations of 1 second each to allow the JIT to warmup and optimize as much as possible,
the next 3 iterations of 1 second each were timed and averaged.
This was then repeated 3 times, with a new JVM each time, and the results were averaged.

The `native`, `native (simplified)`, `native (optimized)` tests each tested a hard-coded method containing the expanded, simplified, and
optimized expression, respectively.

```
Benchmark                                                      (testExpression)   (input)  Mode  Cnt    Score    Error  Units
exp4J                sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9  243.954 ± 15.926  ns/op
parsii               sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9   53.833 ±  1.485  ns/op
native               sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9   31.512 ±  3.792  ns/op
native (simplified)  sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9   22.676 ±  0.738  ns/op
Paralithic           sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9    4.643 ±  0.214  ns/op
native (optimized)   sin(x) + 2 + ((7-5) * (3.14159 * x^(14-10)) + sin(-3.141))     23422  avgt    9    4.443 ±  0.085  ns/op
```

Results are from tests run on an Intel i7-1165G7.

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
