package com.dfsek.paralithic.node.binary.booleans;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.Constant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.DCONST_1;

public class AndNode extends BinaryNode {
    public AndNode(Node left, Node right) {
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
    public Node constantSimplify() {
        return Constant.of((((Constant) left).getValue() != 0 && ((Constant) right).getValue() != 0) ? 1 : 0);
    }

    @Override
    public double eval(double... inputs) {
        return (left.eval(inputs) != 0 && right.eval(inputs) != 0) ? 1 : 0;
    }
}
