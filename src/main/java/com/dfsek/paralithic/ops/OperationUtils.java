package com.dfsek.paralithic.ops;

public class OperationUtils {
    public static Operation simplify(Operation in) {
        if(in instanceof Simplifiable) {
            Simplifiable simplifiable = (Simplifiable) in;
            if(simplifiable.canSimplify()) {
                return simplifiable.simplify();
            }
        }
        return in;
    }
    public static boolean isInt(Class<?> clazz) {
        return int.class.equals(clazz);
    }
    public static boolean isDouble(Class<?> clazz) {
        return double.class.equals(clazz);
    }
    public static boolean isBoolean(Class<?> clazz) {
        return boolean.class.equals(clazz);
    }
    public static boolean isByte(Class<?> clazz) {
        return byte.class.equals(clazz);
    }
    public static boolean isShort(Class<?> clazz) {
        return short.class.equals(clazz);
    }
    public static boolean isLong(Class<?> clazz) {
        return long.class.equals(clazz);
    }
    public static boolean isChar(Class<?> clazz) {
        return char.class.equals(clazz);
    }
    public static boolean isFloat(Class<?> clazz) {
        return float.class.equals(clazz);
    }
    public static boolean isWeakInteger(Class<?> clazz) {
        return isInt(clazz)
                || isByte(clazz)
                || isShort(clazz);
    }

    public static char getDescriptorCharacter(Class<?> clazz) {
        if(isDouble(clazz)) return 'D';
        if(isInt(clazz)) return 'I';
        if(isShort(clazz)) return 'S';
        if(isLong(clazz)) return 'J';
        if(isByte(clazz)) return 'B';
        if(isBoolean(clazz)) return 'Z';
        if(isChar(clazz)) return 'C';
        else throw new IllegalArgumentException("Not a primitive type: " + clazz);
    }
}
