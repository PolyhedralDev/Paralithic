package com.dfsek.paralithic.operations.binary.number;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.binary.BinaryNode;
import com.dfsek.paralithic.operations.binary.CommutativeBinaryNode;
import com.dfsek.paralithic.operations.Constant;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DMUL;

public class MultiplicationNode extends CommutativeBinaryNode {
    public MultiplicationNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    protected BinaryNode newInstance(Node left, Node right) {
        return new MultiplicationNode(left, right);
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
    public Constant constantSimplify() {
        return Constant.of(((Constant) left).getValue() * ((Constant) right).getValue());
    }
}