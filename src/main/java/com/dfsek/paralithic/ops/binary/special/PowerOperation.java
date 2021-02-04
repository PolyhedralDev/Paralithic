package com.dfsek.paralithic.ops.binary.special;

import com.dfsek.paralithic.function.natives.NativeMath;
import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import com.dfsek.paralithic.ops.function.NativeFunctionOperation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

public class PowerOperation extends BinaryOperation {
    public PowerOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        new NativeFunctionOperation(NativeMath.POW, Arrays.asList(left, right)).apply(visitor, generatedImplementationName);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        applyOperand(visitor, generatedImplementationName);
    }

    @Override
    public Op getOp() {
        return Op.POWER;
    }

    @Override
    public Operation simplify() {
        return new DoubleConstant(Math.pow(((DoubleConstant) left).getValue(), ((DoubleConstant) right).getValue()));
    }
}
