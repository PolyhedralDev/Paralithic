package com.dfsek.paralithic.ops.binary.number;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DDIV;

public class DivisionOperation extends BinaryOperation {
    public DivisionOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitInsn(DDIV);
    }

    @Override
    public Op getOp() {
        return Op.DIVIDE;
    }

    @Override
    public Constant<Double> simplify() {
        return new DoubleConstant(((DoubleConstant) left).getValue() / ((DoubleConstant) right).getValue());
    }
}
