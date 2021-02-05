package com.dfsek.paralithic.operations.binary;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.Simplifiable;
import com.dfsek.paralithic.operations.constant.Constant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public abstract class BinaryOperation implements Operation, Simplifiable {
    protected Operation left;
    protected Operation right;

    private boolean sealed = false;

    public BinaryOperation(Operation left, Operation right) {
        this.left = OperationUtils.simplify(left);
        this.right = OperationUtils.simplify(right);
    }
    public abstract void applyOperand(MethodVisitor visitor, String generatedImplementationName);
    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        left.apply(visitor, generatedImplementationName);
        right.apply(visitor, generatedImplementationName);
        applyOperand(visitor, generatedImplementationName);
    }

    public void setLeft(Operation left) {
        this.left = left;
    }

    public void setRight(Operation right) {
        this.right = right;
    }

    public void seal() {
        sealed = true;
    }

    public boolean isSealed() {
        return sealed;
    }

    /**
     * Get whether this binary operation can be simplified. If both parameters are constant the operation may be simplified.
     * @return Whether this operation can be simplified.
     */
    @Override
    public int canSimplify() {
        if(left instanceof Constant && right instanceof Constant) return CONSTANT_OPERANDS;
        return specialSimplify();
    }

    protected int specialSimplify() {
        return NO_SIMPLIFY;
    }

    public Operation getLeft() {
        return left;
    }

    public Operation getRight() {
        return right;
    }

    public abstract Op getOp();

    @Override
    public String toString() {
        return "(" + left.toString() + getOp().toString() + right.toString() + ")";
    }

    /**
     * Enumerates the operations supported by this expression.
     */
    public enum Op {
        ADD(3),
        SUBTRACT(3),
        MULTIPLY(4),
        DIVIDE(4),
        MODULO(4),
        POWER(5),
        LT(2),
        LT_EQ(2),
        EQ(2),
        GT_EQ(2),
        GT(2),
        NEQ(2),
        AND(1),
        OR(1);

        private final int priority;

        Op(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
