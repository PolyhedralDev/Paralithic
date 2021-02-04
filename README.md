# Paralithic
Super fast expression evaluator/parser written in Java

Paralithic is a library for parsing and evaluating mathematical expressions. It uses ASM to dynamically
generate implementations of the `Expression` interface representing the expression, with expressions converted directly
into Java bytecode for maximum performance, and maximum room for JIT optimization. Paralithic also uses several AOT
optimization techniques, such as:
* Evaluating stateless functions with constant arguments at parse time, replacing them with constants.
* Evaluating binary operations with constant operands at parse time, replacing them with constants.

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
The expression `"(2 + ((7-5) * (3.14159 * pow(x, (12-10))) + sin(-3.141)))"` was evaluated
with x ranging from 0 to 1,000,000 in 3 different expression libraries.
The test was run 20 times to allow the JIT to warmup and optimize as much as possible,
then 20 more iterations were timed and averaged. An AMD Ryzen 9 3900X
was used for this test.

The "Aggregate" values are the summed values of all iterations in the test. The aggregate is mainly
to prevent HotSpot JIT from optimizing out calls to evaluation altogether.

```
Testing exp4j...
Avg for 20 iterations of 1000000 evaluations: 165.40143047368423ms
Aggregate: 8.377560766985067E19
Testing Paralithic...
Avg for 20 iterations of 1000000 evaluations: 2.7530026315789473ms
Aggregate: 8.377560766985067E19
Testing parsii...
Avg for 20 iterations of 1000000 evaluations: 37.78850121052632ms
Aggregate: 8.377560766985075E19
```

Paralithic generated the following class from the input function:
```java
public class ExpressionIMPL_0 implements Expression {
    public ExpressionIMPL_0() {
    }

    public double evaluate(double[] var1) {
        return 2.0D + 2.0D * 3.14159D * Math.pow(var1[0], 2.0D) + -5.926535550994539E-4D;
    }
}
```

## License
Paralithic is licensed under the 
[GNU General Public License version 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) (GPLv3).

Paralithic contains code from a [modified version](https://github.com/PolyhedralDev/parsii) of
[Parsii](https://github.com/scireum/parsii), licensed under the [MIT license](https://www.mit.edu/~amini/LICENSE.md).