package com.dfsek.paralithic.operations.binary.number;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.binary.BinaryNode;
import com.dfsek.paralithic.operations.binary.CommutativeBinaryNode;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class AdditionNode extends CommutativeBinaryNode {
    public AdditionNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    protected BinaryNode newInstance(Node left, Node right) {
        return new AdditionNode(left, right);
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
