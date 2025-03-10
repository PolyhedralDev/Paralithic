package com.dfsek.paralithic.node.binary.number;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DREM;


public class ModuloNode extends BinaryNode {
    public ModuloNode(Node left, Node right) {
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
    public Constant constantSimplify() {
        return Constant.of(((Constant) left).getValue() % ((Constant) right).getValue());
    }

    @Override
    public Node finalSimplify() {
        if(left instanceof Constant c) {
            if(c.getValue() == 0) {
                return Constant.of(0);
            }
        }
        if(right instanceof Constant c) {
            if(c.getValue() == 0) {
                return left;
            }
        }
        return super.finalSimplify();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return left.eval(localVariables, inputs) % right.eval(localVariables, inputs);
    }
}
