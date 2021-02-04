package com.dfsek.paralithic.ops.binary.booleans;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.DCONST_1;

public class AndOperation extends BinaryOperation {
    public AndOperation(Operation left, Operation right) {
        super(left, right);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        Label end = new Label();
        Label fail = new Label();

        left.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCONST_0); // Push zero to stack
        visitor.visitInsn(DCMPG); // Compare doubles on stack

        visitor.visitJumpInsn(IFEQ, fail);

        right.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCONST_0);
        visitor.visitInsn(DCMPG);
        visitor.visitJumpInsn(IFEQ, fail);

        visitor.visitInsn(DCONST_1);
        visitor.visitJumpInsn(GOTO, end);
        visitor.visitLabel(fail);
        visitor.visitInsn(DCONST_0);
        visitor.visitLabel(end);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        applyOperand(visitor, generatedImplementationName);
    }

    @Override
    public Op getOp() {
        return Op.AND;
    }

    @Override
    public Operation simplify(int opCode) {
        return new DoubleConstant((((DoubleConstant) left).getValue() != 0 && ((DoubleConstant) right).getValue() != 0) ? 1 : 0);
    }
}
