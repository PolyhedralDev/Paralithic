package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.binary.number.DivisionNode;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;

import java.util.List;
import java.util.Optional;

public class NativeMath {
    private static final Class<?> MATH = Math.class;

    public static NativeFunction MAX = (NativeMathFunction) () -> MATH.getMethod("max", double.class, double.class);
    public static NativeFunction MIN = (NativeMathFunction) () -> MATH.getMethod("min", double.class, double.class);
    public static NativeFunction SIN = (NativeMathFunction) () -> MATH.getMethod("sin", double.class);
    public static NativeFunction COS = (NativeMathFunction) () -> MATH.getMethod("cos", double.class);
    public static NativeFunction TAN = (NativeMathFunction) () -> MATH.getMethod("tan", double.class);
    public static NativeFunction ROUND = (NativeMathFunction) () -> MATH.getMethod("round", double.class);
    public static NativeFunction FLOOR = (NativeMathFunction) () -> MATH.getMethod("floor", double.class);
    public static NativeFunction CEIL = (NativeMathFunction) () -> MATH.getMethod("ceil", double.class);
    public static NativeFunction SQRT = (NativeMathFunction) () -> MATH.getMethod("sqrt", double.class);
    public static NativeFunction POW = ((NativeMathFunction) () -> MATH.getMethod("pow", double.class, double.class)).withSimplifyRule(args -> {
        if(args.get(1) instanceof Constant c) { // constant powers
            double v = c.getValue();
            if(v == 0) {
                return Optional.of(Constant.of(1)); // n^0 == 1
            } else if(v == 1) {
                return Optional.of(args.get(0)); // n^1 == n
            } else if(v == -1) {
                return Optional.of(new DivisionNode(Constant.of(1), args.get(0))); // n^-1 = 1/n
            } else if(v == 0.5) {
                return Optional.of(new NativeFunctionNode(SQRT, List.of(args.get(0)))); // n^0.5 == sqrt(n)
            } else if(v > 0 && Math.floor(v) == v) {
                return Optional.of(new NativeFunctionNode(NativeMath.INT_POW, args));
            }
        }
        if(args.get(0) instanceof Constant c) {
            double v = c.getValue();
            if(v == 1) return Optional.of(Constant.of(1)); // 1^n == 1
        }
        return Optional.empty();
    });
    public static NativeFunction SINH = (NativeMathFunction) () -> MATH.getMethod("sinh", double.class);
    public static NativeFunction COSH = (NativeMathFunction) () -> MATH.getMethod("cosh", double.class);
    public static NativeFunction TANH = (NativeMathFunction) () -> MATH.getMethod("tanh", double.class);
    public static NativeFunction ASIN = (NativeMathFunction) () -> MATH.getMethod("asin", double.class);
    public static NativeFunction ACOS = (NativeMathFunction) () -> MATH.getMethod("acos", double.class);
    public static NativeFunction ATAN = (NativeMathFunction) () -> MATH.getMethod("atan", double.class);
    public static NativeFunction ATAN2 = (NativeMathFunction) () -> MATH.getMethod("atan2", double.class, double.class);
    public static NativeFunction DEG = (NativeMathFunction) () -> MATH.getMethod("toDegrees", double.class);
    public static NativeFunction RAD = (NativeMathFunction) () -> MATH.getMethod("toRadians", double.class);
    public static NativeFunction ABS = (NativeMathFunction) () -> MATH.getMethod("abs", double.class);
    public static NativeFunction LOG = (NativeMathFunction) () -> MATH.getMethod("log10", double.class);
    public static NativeFunction LN = (NativeMathFunction) () -> MATH.getMethod("log", double.class);
    public static NativeFunction EXP = (NativeMathFunction) () -> MATH.getMethod("exp", double.class);
    public static NativeFunction SIGN = (NativeMathFunction) () -> MATH.getMethod("signum", double.class);
    public static NativeFunction SIGMOID = (NativeMathFunction) () -> NativeMath.class.getMethod("sigmoid", double.class, double.class);

    public static NativeFunction INT_POW = (NativeMathFunction) () -> NativeMath.class.getMethod("intPow", double.class, double.class);


    public static double sigmoid(double a, double b) {
        return 1 / (Math.exp(-1 * a * b));
    }

    public static double intPow(double x, double yd) {
        long y = (long) yd;
        double result = 1;
        while (y > 0) {
            if ((y & 1) == 0) {
                x *= x;
                y >>>= 1;
            } else {
                result *= x;
                y--;
            }
        }
        return result;
    }
}
