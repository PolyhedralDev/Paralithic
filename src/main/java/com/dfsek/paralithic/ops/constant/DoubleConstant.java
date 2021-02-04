package com.dfsek.paralithic.ops.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class DoubleConstant extends Constant<Double> {

    public DoubleConstant(double value) {
        super(value);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitLdcInsn(value);
    }
}
