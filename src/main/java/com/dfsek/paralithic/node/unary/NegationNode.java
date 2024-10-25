package com.dfsek.paralithic.node.unary;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.Constant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class NegationNode extends UnaryNode {
    public NegationNode(Node op) {
        super(op);
    }

    @Override
    public void applyOperand(MethodVisitor visitor) {
        visitor.visitInsn(DNEG);
    }

    @Override
    public @NotNull Node simplify() {
        if(op instanceof Constant) {
            return Constant.of(-((Constant) op).getValue());
        }
        return super.simplify();
    }

    @Override
    public String toString() {
        return "-" + op.toString();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return -op.eval(localVariables, inputs);
    }
}
