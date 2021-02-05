package com.dfsek.paralithic.operations.binary.booleans;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.binary.BinaryOperation;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.GOTO;

public class OrOperation extends BinaryOperation {
    public OrOperation(Operation left, Operation right) {
        super(left, right);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        Label end = new Label();
        Label shortcut = new Label();
        left.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCONST_0); // Push zero to stack
        visitor.visitInsn(DCMPG); // Compare doubles on stack

        visitor.visitJumpInsn(IFNE, shortcut);

        right.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCONST_0);
        visitor.visitInsn(DCMPG);
        visitor.visitJumpInsn(IFNE, shortcut);

        visitor.visitInsn(DCONST_0);
        visitor.visitJumpInsn(GOTO, end);
        visitor.visitLabel(shortcut);
        visitor.visitInsn(DCONST_1);
        visitor.visitLabel(end);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        applyOperand(visitor, generatedImplementationName);
    }

    @Override
    public Op getOp() {
        return Op.OR;
    }

    @Override
    public Operation simplify(int opCode) {
        return new DoubleConstant((((DoubleConstant) left).getValue() != 0 && ((DoubleConstant) right).getValue() != 0) ? 1 : 0);
    }
}
