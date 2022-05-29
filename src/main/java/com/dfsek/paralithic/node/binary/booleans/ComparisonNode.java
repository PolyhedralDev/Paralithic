package com.dfsek.paralithic.node.binary.booleans;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.Constant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ComparisonNode extends BinaryNode {
    private final Op op;

    public ComparisonNode(Node left, Node right, Op op) {
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
    public double eval(double... inputs) {
        double l = left.eval(inputs);
        double r = right.eval(inputs);
        switch(op) {
            case EQ:
                return l == r ? 1 : 0;
            case GT:
                return l > r ? 1 : 0;
            case LT:
                return l < r ? 1 : 0;
            case NEQ:
                return l != r ? 1 : 0;
            case GT_EQ:
                return l >= r ? 1 : 0;
            case LT_EQ:
                return l <= r ? 1 : 0;
            default:
                throw new IllegalArgumentException("Not comparison: " + op);
        }
    }

    @Override
    public Op getOp() {
        return op;
    }

    @Override
    public Node constantSimplify() {
        double l = ((Constant) left).getValue();
        double r = ((Constant) right).getValue();
        switch(op) {
            case EQ:
                return Constant.of(l == r ? 1 : 0);
            case GT:
                return Constant.of(l > r ? 1 : 0);
            case LT:
                return Constant.of(l < r ? 1 : 0);
            case NEQ:
                return Constant.of(l != r ? 1 : 0);
            case GT_EQ:
                return Constant.of(l >= r ? 1 : 0);
            case LT_EQ:
                return Constant.of(l <= r ? 1 : 0);
            default:
                throw new IllegalArgumentException("Not comparison: " + op);
        }
    }
}
