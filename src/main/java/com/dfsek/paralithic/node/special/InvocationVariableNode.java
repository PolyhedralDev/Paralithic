package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class InvocationVariableNode implements Node {
    private final int index;

    public InvocationVariableNode(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "LOCAL_" + index + "";
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitVarInsn(ALOAD, 2); // Load array ref (ref 0 is "this" ref, 1 is first argument, 2 is second, which is array)
        NodeUtils.siPush(visitor, index); // Push index to stack
        visitor.visitInsn(DALOAD); // Pop index; push value to stack
    }

    @Override
    public Statefulness statefulness() {
        return Statefulness.STATELESS; // Invocation variables are stateless.
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return inputs[index];
    }
}
