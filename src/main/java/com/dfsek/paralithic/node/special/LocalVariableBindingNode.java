package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVariableBindingNode implements Node {

    private final int index;
    private final Node boundExpression;
    private final Node expression;

    public LocalVariableBindingNode(int index, Node boundExpression, Node expression) {
        this.index = NodeUtils.getLocalVariableIndex(index);
        this.boundExpression = boundExpression;
        this.expression = expression;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        boundExpression.apply(visitor, generatedImplementationName);
        visitor.visitVarInsn(Opcodes.DSTORE, index);
        expression.apply(visitor, generatedImplementationName);
    }

    @Override
    public Statefulness statefulness() {
        return null;
    }

    @Override
    public double eval(double... inputs) {
        return expression.eval(inputs);
    }
}
