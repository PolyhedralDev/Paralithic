package com.dfsek.paralithic.operations.unary;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.Constant;
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
    public Node simplify() {
        if(op instanceof Constant) {
            return Constant.of(-((Constant) op).getValue());
        }
        return super.simplify();
    }

    @Override
    public String toString() {
        return "-" + op.toString();
    }
}
