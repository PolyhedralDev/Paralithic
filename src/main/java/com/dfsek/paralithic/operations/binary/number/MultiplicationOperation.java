package com.dfsek.paralithic.operations.binary.number;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.binary.BinaryOperation;
import com.dfsek.paralithic.operations.binary.CommutativeBinaryOperation;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DMUL;

public class MultiplicationOperation extends CommutativeBinaryOperation {
    public MultiplicationOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    protected BinaryOperation newInstance(Operation left, Operation right) {
        return new MultiplicationOperation(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitInsn(DMUL);
    }

    @Override
    public Op getOp() {
        return Op.MULTIPLY;
    }

    @Override
    public Constant<Double> constantSimplify() {
        return new DoubleConstant(((DoubleConstant) left).getValue() * ((DoubleConstant) right).getValue());
    }
}
