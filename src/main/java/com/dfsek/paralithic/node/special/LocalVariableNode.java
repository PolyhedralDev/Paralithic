package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVariableNode implements Node {

    private final int index;

    public LocalVariableNode(int index) {
        this.index = index;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitVarInsn(Opcodes.DLOAD, NodeUtils.getLocalVariableIndex(index));
    }

    @Override
    public Statefulness statefulness() {
        return Statefulness.STATELESS;
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return localVariables[index];
    }
}
