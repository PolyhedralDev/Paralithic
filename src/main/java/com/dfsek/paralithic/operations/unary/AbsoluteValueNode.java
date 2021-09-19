package com.dfsek.paralithic.operations.unary;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class AbsoluteValueNode extends UnaryNode {
    public AbsoluteValueNode(Node op) {
        super(op);
    }

    @Override
    public void applyOperand(MethodVisitor visitor) {
        Label endIf = new Label();
        visitor.visitInsn(DUP2); // Duplicate value on stack.
        visitor.visitInsn(DCONST_0); // Push 0.0 to stack.
        visitor.visitInsn(DCMPG); // Compare doubles on stack
        visitor.visitJumpInsn(IFGE, endIf); // Jump to end if value is greater than zero
        visitor.visitInsn(DNEG); // Negate double
        visitor.visitLabel(endIf);
    }

    @Override
    public Node simplify() {
        return new DoubleConstant(Math.abs(((DoubleConstant) op).getValue()));
    }

    @Override
    public String toString() {
        return "|" + op.toString() + "|";
    }
}
