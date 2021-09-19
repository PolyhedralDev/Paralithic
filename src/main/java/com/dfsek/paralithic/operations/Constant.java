package com.dfsek.paralithic.operations;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Constant implements Simplifiable {
    protected final double value;

    public static final Constant DCONST_0 = new Constant(0) {
        @Override
        public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
            visitor.visitInsn(Opcodes.DCONST_0);
        }

        @Override
        public @NotNull Node simplify() {
            return this;
        }
    };

    public static final Constant DCONST_1 = new Constant(1) {
        @Override
        public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
            visitor.visitInsn(Opcodes.DCONST_1);
        }

        @Override
        public @NotNull Node simplify() {
            return this;
        }
    };

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

    @Override
    public @NotNull Node simplify() {
        if(value == 0) return DCONST_0;
        if(value == 1) return DCONST_1;
        return this;
    }
}
