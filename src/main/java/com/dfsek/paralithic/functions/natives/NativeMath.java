package com.dfsek.paralithic.functions.natives;

public class NativeMath {
    private static final Class<?> MATH = Math.class;
    public static NativeFunction POW = (NativeMathFunction) () -> MATH.getMethod("pow", double.class, double.class);
    public static NativeFunction MAX = (NativeMathFunction) () -> NativeMath.class.getMethod("fastMax", double.class, double.class);
    public static NativeFunction MIN = (NativeMathFunction) () -> NativeMath.class.getMethod("fastMin", double.class, double.class);
    public static NativeFunction SIN = (NativeMathFunction) () -> MATH.getMethod("sin", double.class);
    public static NativeFunction COS = (NativeMathFunction) () -> MATH.getMethod("cos", double.class);
    public static NativeFunction TAN = (NativeMathFunction) () -> MATH.getMethod("tan", double.class);
    public static NativeFunction ROUND = (NativeMathFunction) () -> MATH.getMethod("round", double.class);
    public static NativeFunction FLOOR = (NativeMathFunction) () -> NativeMath.class.getMethod("fastFloor", double.class);
    public static NativeFunction CEIL = (NativeMathFunction) () -> NativeMath.class.getMethod("fastCeil", double.class);
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
    public static NativeFunction ABS = (NativeMathFunction) () -> NativeMath.class.getMethod("fastAbs", double.class);
    public static NativeFunction LOG = (NativeMathFunction) () -> MATH.getMethod("log10", double.class);
    public static NativeFunction LN = (NativeMathFunction) () -> MATH.getMethod("log", double.class);
    public static NativeFunction EXP = (NativeMathFunction) () -> MATH.getMethod("exp", double.class);
    public static NativeFunction SIGN = (NativeMathFunction) () -> MATH.getMethod("signum", double.class);
    public static NativeFunction SIGMOID = (NativeMathFunction) () -> NativeMath.class.getMethod("sigmoid", double.class, double.class);
    public static NativeFunction POW2 = (NativeMathFunction) () -> NativeMath.class.getMethod("pow2", double.class);

    public static double pow2(double a) {
        return a*a;
    }

    public static double sigmoid(double a, double b) {
        return 1 / (Math.exp(-1 * a * b));
    }

    @SuppressWarnings("ManualMinMaxCalculation")
    public static double fastMin(double a, double b) {
        return a < b ? a : b;
    }

    @SuppressWarnings("ManualMinMaxCalculation")
    public static double fastMax(double a, double b) {
        return a > b ? a : b;
    }

    public static double fastAbs(double f) {
        return f < 0 ? -f : f;
    }

    public static int fastCeil(double f) {
        int i = (int) f;
        if(i < f) i++;
        return i;
    }

    public static int fastFloor(double f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }
}
