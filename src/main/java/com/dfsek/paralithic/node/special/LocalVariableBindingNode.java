package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Simplifiable;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class LocalVariableBindingNode implements Simplifiable {

    private final int index;
    private Node boundExpression;
    private Node expression;

    private final Lazy<Statefulness> statefulness = Lazy.of(
        () -> Statefulness.combine(boundExpression.statefulness(), expression.statefulness()));

    public LocalVariableBindingNode(int index, Node boundExpression, Node expression) {
        this.index = index;
        this.boundExpression = boundExpression;
        this.expression = expression;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        boundExpression.apply(visitor, generatedImplementationName);
        visitor.visitVarInsn(Opcodes.DSTORE, NodeUtils.getLocalVariableIndex(index));
        expression.apply(visitor, generatedImplementationName);
    }

    @Override
    public Statefulness statefulness() {
        return statefulness.get();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        localVariables[index] = boundExpression.eval(localVariables, inputs);
        return expression.eval(localVariables, inputs);
    }

    @Override
    public @NotNull Node simplify() {
        boundExpression = NodeUtils.simplify(boundExpression);

        // A dead code optimization could be made here if the local variable is never
        // referenced inside `this.expression` however such an optimization would add
        // extra complexity (possibly via some kind of ref-counting mechanism) for
        // something that JIT will likely handle well anyway.
        expression = NodeUtils.simplify(expression);

        statefulness.invalidate();

        if(expression instanceof Constant)
            return expression;

        return this;
    }
}
