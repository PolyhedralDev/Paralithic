package com.dfsek.paralithic.node.binary.booleans;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DCMPG;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DCONST_1;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFNE;


public class OrNode extends BinaryNode {
    public OrNode(Node left, Node right) {
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
    public Node constantSimplify() {
        return Constant.of((((Constant) left).getValue() != 0 || ((Constant) right).getValue() != 0) ? 1 : 0);
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return (left.eval(localVariables, inputs) != 0 || right.eval(localVariables, inputs) != 0) ? 1 : 0;
    }
}
