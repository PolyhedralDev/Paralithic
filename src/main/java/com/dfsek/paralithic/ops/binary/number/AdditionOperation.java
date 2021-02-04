package com.dfsek.paralithic.ops.binary.number;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.binary.CommutativeBinaryOperation;
import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class AdditionOperation extends CommutativeBinaryOperation {
    public AdditionOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    protected BinaryOperation newInstance(Operation left, Operation right) {
        return new AdditionOperation(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitInsn(DADD);
    }

    @Override
    public Op getOp() {
        return Op.ADD;
    }

    @Override
    public Constant<Double> constantSimplify() {
        return new DoubleConstant(((DoubleConstant) left).getValue() + ((DoubleConstant) right).getValue());
    }
}
