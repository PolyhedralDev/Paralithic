package com.dfsek.paralithic.node.binary.number;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.Constant;
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
    public Constant constantSimplify() {
        return Constant.of(((Constant) left).getValue() / ((Constant) right).getValue());
    }

    @Override
    public double eval(double... inputs) {
        return left.eval(inputs) / right.eval(inputs);
    }
}
