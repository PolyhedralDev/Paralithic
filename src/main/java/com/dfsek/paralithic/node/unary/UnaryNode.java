package com.dfsek.paralithic.node.unary;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.OperationUtils;
import com.dfsek.paralithic.node.Simplifiable;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public abstract class UnaryNode implements Simplifiable {
    protected Node op;

    protected UnaryNode(Node op) {
        this.op = op;
    }

    public abstract void applyOperand(MethodVisitor visitor);

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        op.apply(visitor, generatedImplementationName); // Push operand result to stack
        applyOperand(visitor); // Apply operator
    }

    @Override
    public @NotNull Node simplify() {
        this.op = OperationUtils.simplify(op);
        return this;
    }
}
