package com.dfsek.paralithic.operations.binary.number;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.binary.BinaryOperation;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DSUB;

public class SubtractionOperation extends BinaryOperation {
    public SubtractionOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitInsn(DSUB);
    }

    @Override
    public Op getOp() {
        return Op.SUBTRACT;
    }

    @Override
    public Constant<Double> simplify(int opCode) {
        return new DoubleConstant(((DoubleConstant) left).getValue() - ((DoubleConstant) right).getValue());
    }
}
