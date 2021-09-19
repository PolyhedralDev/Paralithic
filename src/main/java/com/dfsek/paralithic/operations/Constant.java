package com.dfsek.paralithic.operations;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class Constant implements Node {
    protected final double value;

    private Constant(double value) {
        this.value = value;
    }

    public static Constant of(double value) {
        return new Constant(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitLdcInsn(value);
    }
}
