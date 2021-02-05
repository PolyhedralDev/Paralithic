package com.dfsek.paralithic.functions.natives;

import java.lang.reflect.Method;

public class NativeMath {
    private static final Class<?> MATH = Math.class;
    public static NativeFunction POW = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("pow", double.class, double.class);
        }
    };
    public static NativeFunction MAX = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("max", double.class, double.class);
        }
    };
    public static NativeFunction MIN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("min", double.class, double.class);
        }
    };
    public static NativeFunction SIN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("sin", double.class);
        }
    };
    public static NativeFunction COS = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("cos", double.class);
        }
    };
    public static NativeFunction TAN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("tan", double.class);
        }
    };
    public static NativeFunction ROUND = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("round", double.class);
        }
    };
    public static NativeFunction FLOOR = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("floor", double.class);
        }
    };
    public static NativeFunction CEIL = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("ceil", double.class);
        }
    };
    public static NativeFunction SQRT = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("sqrt", double.class);
        }
    };
    public static NativeFunction SINH = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("sinh", double.class);
        }
    };
    public static NativeFunction COSH = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("cosh", double.class);
        }
    };
    public static NativeFunction TANH = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("tanh", double.class);
        }
    };
    public static NativeFunction ASIN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("asin", double.class);
        }
    };
    public static NativeFunction ACOS = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("acos", double.class);
        }
    };
    public static NativeFunction ATAN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("atan", double.class);
        }
    };
    public static NativeFunction ATAN2 = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("atan2", double.class, double.class);
        }
    };
    public static NativeFunction DEG = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("toDegrees", double.class);
        }
    };
    public static NativeFunction RAD = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("toRadians", double.class);
        }
    };
    public static NativeFunction ABS = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("abs", double.class);
        }
    };
    public static NativeFunction LOG = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("log10", double.class);
        }
    };
    public static NativeFunction LN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("log", double.class);
        }
    };
    public static NativeFunction EXP = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("exp", double.class);
        }
    };
    public static NativeFunction SIGN = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return MATH.getMethod("signum", double.class);
        }
    };
    public static NativeFunction SIGMOID = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return NativeMath.class.getMethod("sigmoid", double.class, double.class);
        }
    };
    public static NativeFunction POW2 = new NativeMathFunction() {
        @Override
        public Method getMethod() throws NoSuchMethodException {
            return NativeMath.class.getMethod("pow2", double.class);
        }
    };

    public static double pow2(double a) {
        return a*a;
    }

    public static double sigmoid(double a, double b) {
        return 1 / (Math.exp(-1 * a * b));
    }
}
