package com.dfsek.paralithic.ops.unary;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class NegationOperation extends UnaryOperation {
    public NegationOperation(Operation op) {
        super(op);
    }

    @Override
    public void applyOperand(MethodVisitor visitor) {
        visitor.visitInsn(DNEG);
    }

    @Override
    public Operation simplify() {
        return new DoubleConstant(-((DoubleConstant) op).getValue());
    }

    @Override
    public String toString() {
        return "-" + op.toString();
    }
}
