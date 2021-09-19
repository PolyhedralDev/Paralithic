package com.dfsek.paralithic.operations.binary;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.Simplifiable;
import com.dfsek.paralithic.operations.Constant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public abstract class BinaryNode implements Simplifiable {
    protected Node left;
    protected Node right;

    private boolean sealed = false;

    public BinaryNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }
    public abstract void applyOperand(MethodVisitor visitor, String generatedImplementationName);
    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        left.apply(visitor, generatedImplementationName);
        right.apply(visitor, generatedImplementationName);
        applyOperand(visitor, generatedImplementationName);
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void seal() {
        sealed = true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSealed() {
        return sealed;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public abstract Op getOp();

    @Override
    public String toString() {
        return "(" + left.toString() + getOp().toString() + right.toString() + ")";
    }

    public abstract Node constantSimplify();

    @Override
    public @NotNull Node simplify() {
        this.left = OperationUtils.simplify(left);
        this.right = OperationUtils.simplify(right);
        if(left instanceof Constant && right instanceof Constant) {
            return constantSimplify();
        }
        return this;
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