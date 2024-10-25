package com.dfsek.paralithic.node.binary.number;

import com.dfsek.paralithic.functions.natives.NativeMath;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

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
    public Node finalSimplify() {
        if(left instanceof Constant c) {
            if(c.getValue() == 0) {
                return Constant.of(0);
            }
        }
        if(right instanceof Constant c) {
            if(c.getValue() == 1) {
                return left;
            }
            if(c.getValue() != 0) {
                return new MultiplicationNode(left, Constant.of(1 / c.getValue()));
            }
        }
        return super.finalSimplify();
    }

    @Override
    public @NotNull Node finalOptimize() {
        if(right instanceof Constant c) {
            if(c.getValue() == 0) {
                return new NativeFunctionNode(NativeMath.getNativeMathFunction("copy_sign"), List.of(left, Constant.of(Double.POSITIVE_INFINITY)));
            }
        }
        try {
            if(right instanceof NativeFunctionNode n && NativeMath.getNativeMathFunctionTable()
                                                                                          .get("sqrt")
                                                                                          .getMethod() == n.getFunction().getMethod()) {
                NativeFunctionNode invSqrt = new NativeFunctionNode(NativeMath.getNativeMathFunction("inv_sqrt"), n.getArgs());
                if(left instanceof Constant c && c.getValue() == 1) {
                    return invSqrt;
                }
                return new MultiplicationNode(left, invSqrt);
            }
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return super.finalOptimize();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return left.eval(localVariables, inputs) / right.eval(localVariables, inputs);
    }
}
