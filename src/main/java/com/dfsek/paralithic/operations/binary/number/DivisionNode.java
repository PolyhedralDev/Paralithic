package com.dfsek.paralithic.operations.binary.number;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.binary.BinaryNode;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DDIV;

public class DivisionNode extends BinaryNode {
    public DivisionNode(Node left, Node right) {
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
    public Constant<Double> simplify(int opCode) {
        return new DoubleConstant(((DoubleConstant) left).getValue() / ((DoubleConstant) right).getValue());
    }
}
