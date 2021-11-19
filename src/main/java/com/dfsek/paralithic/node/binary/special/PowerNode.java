package com.dfsek.paralithic.node.binary.special;

import com.dfsek.paralithic.functions.natives.NativeMath;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.binary.BinaryNode;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.Collections;

public class PowerNode extends BinaryNode {
    public PowerNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public void applyOperand(MethodVisitor visitor, String generatedImplementationName) {
        new NativeFunctionNode(NativeMath.POW, Arrays.asList(left, right)).apply(visitor, generatedImplementationName);
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        applyOperand(visitor, generatedImplementationName);
    }

    @Override
    public Op getOp() {
        return Op.POWER;
    }

    @Override
    public @NotNull Node finalSimplify() {
        if(right instanceof Constant) {
            double pow = ((Constant) right).getValue();
            if(pow == 0) {
                return Constant.of(1); // n^0 == 1
            } else if(pow == 1) {
                return left; // n^1 == n
            } else if(pow == 2) {
                return new NativeFunctionNode(NativeMath.POW2, Collections.singletonList(left));
            } else if(pow == 0.5) {
                return new NativeFunctionNode(NativeMath.SQRT, Collections.singletonList(left)); // n^0.5 == sqrt(n)
            } else if(pow > 0 && NativeMath.fastFloor(pow) == pow) {
                return new NativeFunctionNode(NativeMath.INT_POW, Arrays.asList(left, right));
            }
        }
        return this;
    }

    @Override
    public Node constantSimplify() {
        return Constant.of(Math.pow(((Constant) left).getValue(), ((Constant) right).getValue()));
    }
}
