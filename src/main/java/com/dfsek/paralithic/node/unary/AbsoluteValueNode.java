package com.dfsek.paralithic.node.unary;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import org.jetbrains.annotations.NotNull;
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
    public @NotNull Node simplify() {
        if (op instanceof Constant) {
            return Constant.of(Math.abs(((Constant) op).getValue()));
        }
        if (op instanceof AbsoluteValueNode) {
            return op;
        }
        return super.simplify();
    }

    @Override
    public String toString() {
        return "|" + op.toString() + "|";
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return Math.abs(op.eval(localVariables, inputs));
    }
}
