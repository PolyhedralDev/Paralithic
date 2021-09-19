package com.dfsek.paralithic.node;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class NodeUtils {
    public static Node simplify(Node in) {
        if(in instanceof Simplifiable) {
            return ((Simplifiable) in).simplify();
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

    /**
     * Get the descriptor character for a primitive class.
     * @param clazz CLass to get descriptor char for
     * @return Descriptor character
     */
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

    /**
     * Push an integer to the stack, using integer constant instructions (ICONST_M1-5) if available, else using SIPUSH.
     * @param visitor MethodVisitor to apply instruction to
     * @param i Integer to push
     */
    public static void siPush(MethodVisitor visitor, int i) {
        switch(i) {
            case  -1:
                visitor.visitInsn(ICONST_M1);
                return;
            case 0:
                visitor.visitInsn(ICONST_0);
                return;
            case 1:
                visitor.visitInsn(ICONST_1);
                return;
            case 2:
                visitor.visitInsn(ICONST_2);
                return;
            case 3:
                visitor.visitInsn(ICONST_3);
                return;
            case 4:
                visitor.visitInsn(ICONST_4);
                return;
            case 5:
                visitor.visitInsn(ICONST_5);
                return;
            default:
                visitor.visitIntInsn(SIPUSH, i);
        }
    }
}
