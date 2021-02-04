package com.dfsek.paralithic.ops.binary.number;

import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.binary.BinaryOperation;
import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DREM;

public class ModuloOperation extends BinaryOperation {
    public ModuloOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitInsn(DREM);
    }

    @Override
    public Op getOp() {
        return Op.MODULO;
    }

    @Override
    public Constant<Double> simplify(int opCode) {
        return new DoubleConstant(((DoubleConstant) left).getValue() % ((DoubleConstant) right).getValue());
    }
}
