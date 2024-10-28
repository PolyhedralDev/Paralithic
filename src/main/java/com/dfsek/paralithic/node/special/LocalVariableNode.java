package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LocalVariableNode implements Node {
    // This could implement `Simplifiable` and be constant folded if the evaluated
    // value in the binding is constant, however this adds extra complexity for an
    // optimisation that JIT should easily handle. Implementing such an optimisation
    // would require maintaining a `LocalVariableBindingNode[]` context passed through
    // each `Simplifiable#simplify()` call, which was trialed prior to writing this
    // comment.

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
        // Assume stateless, which should allow the statefulness of the node
        // that this variable is bound to take precedence, which should be
        // a parent node of this node in a well-formed expression. Alternatively
        // a way of linking each local variable node with its corresponding binding
        // node could be implemented if constant folding were such that the
        // statefulness here depends on the binding node, however doing that would
        // also be part of the process of implementing constant folding in this class,
        // which is not implemented for the reasons stated above.
        return Statefulness.STATELESS;
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return localVariables[index];
    }
}
