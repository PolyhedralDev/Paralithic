package com.dfsek.paralithic.ops.binary.booleans;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ComparisonOperation extends BinaryOperation {
    private final Op op;

    public ComparisonOperation(Operation left, Operation right, Op op) {
        super(left, right);
        this.op = op;
    }

    private static int toInstruction(Op op) {
        switch(op) {
            case EQ:
                return IFEQ;
            case GT:
                return IFGT;
            case LT:
                return IFLT;
            case NEQ:
                return IFNE;
            case GT_EQ:
                return IFGE;
            case LT_EQ:
                return IFLE;
            default:
                throw new IllegalArgumentException("Not comparison: " + op);
        }
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        Label endIf = new Label();
        Label valid = new Label();
        left.apply(visitor, generatedImplementationName);
        right.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCMPG); // Compare doubles on stack
        visitor.visitJumpInsn(toInstruction(op), valid); // Jump to end if value matches operator
        visitor.visitInsn(DCONST_0);
        visitor.visitJumpInsn(GOTO, endIf);
        visitor.visitLabel(valid);
        visitor.visitInsn(DCONST_1);
        visitor.visitLabel(endIf);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        applyOperand(visitor, generatedImplementationName);
    }

    @Override
    public Op getOp() {
        return op;
    }

    @Override
    public Operation simplify() {
        double l = ((DoubleConstant) left).getValue();
        double r = ((DoubleConstant) right).getValue();
        switch(op) {
            case EQ:
                return new DoubleConstant(l == r ? 1 : 0);
            case GT:
                return new DoubleConstant(l > r ? 1 : 0);
            case LT:
                return new DoubleConstant(l < r ? 1 : 0);
            case NEQ:
                return new DoubleConstant(l != r ? 1 : 0);
            case GT_EQ:
                return new DoubleConstant(l >= r ? 1 : 0);
            case LT_EQ:
                return new DoubleConstant(l <= r ? 1 : 0);
            default:
                throw new IllegalArgumentException("Not comparison: " + op);
        }
    }
}
