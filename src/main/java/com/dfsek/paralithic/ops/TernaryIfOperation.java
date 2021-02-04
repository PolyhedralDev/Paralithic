package com.dfsek.paralithic.ops;

import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class TernaryIfOperation implements Operation, Simplifiable {
    private final Operation predicate;
    private final Operation left;
    private final Operation right;

    public TernaryIfOperation(Operation predicate, Operation left, Operation right) {
        this.predicate = OperationUtils.simplify(predicate);
        this.left = OperationUtils.simplify(left);
        this.right = OperationUtils.simplify(right);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        Label equal = new Label();
        Label endIf = new Label();
        predicate.apply(visitor, generatedImplementationName);
        visitor.visitLdcInsn(0.0); // Push 0.0 to stack.
        visitor.visitInsn(DCMPG); // Compare doubles on stack
        visitor.visitJumpInsn(IFEQ, equal); // Jump to less label if value is less than zero.
        left.apply(visitor, generatedImplementationName); // Not equal to zero
        visitor.visitJumpInsn(GOTO, endIf);
        visitor.visitLabel(equal);
        right.apply(visitor, generatedImplementationName); // Equal to zero
        visitor.visitLabel(endIf);
    }

    @Override
    public int canSimplify() {
        if(predicate instanceof Constant) return CONSTANT_PREDICATE;
        return NO_SIMPLIFY;
    }

    @Override
    public Operation simplify(int opCode) {
        return ((DoubleConstant) predicate).getValue() != 0 ? left : right;
    }
}
