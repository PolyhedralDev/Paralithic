package com.dfsek.paralithic.ops.binary.special;

import com.dfsek.paralithic.function.natives.NativeMath;
import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.binary.number.MultiplicationOperation;
import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import com.dfsek.paralithic.ops.function.NativeFunctionOperation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.Collections;

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
    public Operation simplify(int opCode) {
        if(opCode == POW_0) return new DoubleConstant(1); // x ^ 0 = 1
        if(opCode == POW_1) return right; // x ^ 1 = x
        if(opCode == POW_2) return new NativeFunctionOperation(NativeMath.POW2, Collections.singletonList(left));
        return new DoubleConstant(Math.pow(((DoubleConstant) left).getValue(), ((DoubleConstant) right).getValue()));
    }

    @Override
    protected int specialSimplify() {
        if(right instanceof Constant) {
            double pow = ((DoubleConstant) right).getValue();
            if(pow == 0) return POW_0;
            if(pow == 1) return POW_1;
            if(pow == 2) return POW_2;
        }
        return NO_SIMPLIFY;
    }
}
