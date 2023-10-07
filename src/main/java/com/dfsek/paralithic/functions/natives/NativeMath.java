package com.dfsek.paralithic.functions.natives;

public class NativeMath {
    private static final Class<?> MATH = Math.class;
    public static NativeFunction POW = (NativeMathFunction) () -> MATH.getMethod("pow", double.class, double.class);
    public static NativeFunction MAX = (NativeMathFunction) () -> MATH.getMethod("max", double.class, double.class);
    public static NativeFunction MIN = (NativeMathFunction) () -> MATH.getMethod("min", double.class, double.class);
    public static NativeFunction SIN = (NativeMathFunction) () -> MATH.getMethod("sin", double.class);
    public static NativeFunction COS = (NativeMathFunction) () -> MATH.getMethod("cos", double.class);
    public static NativeFunction TAN = (NativeMathFunction) () -> MATH.getMethod("tan", double.class);
    public static NativeFunction ROUND = (NativeMathFunction) () -> MATH.getMethod("round", double.class);
    public static NativeFunction FLOOR = (NativeMathFunction) () -> MATH.getMethod("floor", double.class);
    public static NativeFunction CEIL = (NativeMathFunction) () -> MATH.getMethod("ceil", double.class);
    public static NativeFunction SQRT = (NativeMathFunction) () -> MATH.getMethod("sqrt", double.class);
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
    public static NativeFunction POW2 = (NativeMathFunction) () -> NativeMath.class.getMethod("pow2", double.class); //DEPRECATED

    public static NativeFunction INT_POW = (NativeMathFunction) () -> NativeMath.class.getMethod("intPow", double.class, double.class);

    public static double pow2(double a) {
        return a*a;
    } //DEPRECATED

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
