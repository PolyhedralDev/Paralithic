package com.dfsek.paralithic.node.binary;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Optimizable;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;


public abstract class BinaryNode implements Optimizable {
    protected Node left;
    protected Node right;

    private final Lazy<Statefulness> statefulness = Lazy.of(() -> Statefulness.combine(left.statefulness(), right.statefulness()));
        // Cache statefulness.

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

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public abstract Op getOp();

    @Override
    public String toString() {
        return "(" + left.toString() + getOp().toString() + right.toString() + ")";
    }

    public abstract Node constantSimplify();

    public Node finalSimplify() {
        return this;
    }

    public Node finalOptimize() {
        return this;
    }

    @Override
    public @NotNull Node optimize() {
        left = NodeUtils.optimize(left);
        right = NodeUtils.optimize(right);
        statefulness.invalidate(); // Nodes have changed.
        return finalOptimize();
    }

    @Override
    public @NotNull Node simplify() {
        this.left = NodeUtils.simplify(left);
        this.right = NodeUtils.simplify(right);
        statefulness.invalidate(); // Nodes have changed.
        if(left instanceof Constant && right instanceof Constant) {
            return constantSimplify();
        }
        return finalSimplify();
    }

    @Override
    public @NotNull Statefulness statefulness() {
        return statefulness.get();
    }

    /**
     * Enumerates the operations supported by this expression.
     */
    public enum Op {
        ADD(3, "+"),
        SUBTRACT(3, "-"),
        MULTIPLY(4, "*"),
        DIVIDE(4, "/"),
        MODULO(4, "%"),
        POWER(5, "^"),
        LT(2, "<"),
        LT_EQ(2, "<="),
        EQ(2, "="),
        GT_EQ(2, ">="),
        GT(2, ">"),
        NEQ(2, "!="),
        AND(1, "&&"),
        OR(1, "||");

        private final int priority;
        private final String op;

        Op(int priority, String op) {
            this.priority = priority;
            this.op = op;
        }

        public int getPriority() {
            return priority;
        }


        @Override
        public String toString() {
            return op;
        }
    }
}
