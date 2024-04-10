package com.dfsek.paralithic.node.binary.number;

import com.dfsek.paralithic.functions.natives.NativeMath;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.binary.CommutativeBinaryNode;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;
import com.dfsek.paralithic.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

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
    public Constant constantSimplify() {
        return Constant.of(((Constant) left).getValue() + ((Constant) right).getValue());
    }

    @Override
    public @NotNull Node finalSimplify() {
        if(Constants.HAS_FAST_SCALAR_FMA && left instanceof MultiplicationNode m) {
            return new NativeFunctionNode(NativeMath.FMA, List.of(m.getLeft(), m.getRight(), right));
        }
        if(Constants.HAS_FAST_SCALAR_FMA && right instanceof MultiplicationNode m) {
            return new NativeFunctionNode(NativeMath.FMA, List.of(m.getLeft(), m.getRight(), left));
        }
        if(left instanceof Constant c && c.getValue() == 0) {
            return right;
        }
        if(right instanceof Constant c && c.getValue() == 0) {
            return left;
        }
        return super.finalSimplify();
    }

    @Override
    public double eval(double... inputs) {
        return left.eval(inputs) + right.eval(inputs);
    }
}
