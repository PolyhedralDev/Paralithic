package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Optimizable;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DCMPG;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;


public class TernaryIfNode implements Optimizable {
    private Node predicate;
    private Node left;
    private Node right;

    private final Lazy<Statefulness> statefulness = Lazy.of(
        () -> Statefulness.combine(predicate.statefulness(), left.statefulness(), right.statefulness())); // Cache statefulness.


    public TernaryIfNode(Node predicate, Node left, Node right) {
        this.predicate = predicate;
        this.left = left;
        this.right = right;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        Label equal = new Label();
        Label endIf = new Label();
        predicate.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DCONST_0); // Push 0.0 to stack.
        visitor.visitInsn(DCMPG); // Compare doubles on stack
        visitor.visitJumpInsn(IFEQ, equal); // Jump to less label if value is less than zero.
        left.apply(visitor, generatedImplementationName); // Not equal to zero
        visitor.visitJumpInsn(GOTO, endIf);
        visitor.visitLabel(equal);
        right.apply(visitor, generatedImplementationName); // Equal to zero
        visitor.visitLabel(endIf);
    }

    @Override
    public @NotNull Statefulness statefulness() {
        return statefulness.get();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        return predicate.eval(localVariables, inputs) != 0 ? left.eval(localVariables, inputs) : right.eval(localVariables, inputs);
    }

    @Override
    public @NotNull Node simplify() {
        this.predicate = NodeUtils.simplify(predicate);
        this.left = NodeUtils.simplify(left);
        this.right = NodeUtils.simplify(right);
        statefulness.invalidate();
        if(predicate instanceof Constant) {
            return ((Constant) predicate).getValue() != 0 ? left : right;
        }
        if(left instanceof Constant l && right instanceof Constant r) {
            return l.getValue() == r.getValue() ? l : this;
        }
        return this;
    }

    @Override
    public @NotNull Node optimize() {
        this.predicate = NodeUtils.optimize(predicate);
        this.left = NodeUtils.optimize(left);
        this.right = NodeUtils.optimize(right);
        return this;
    }

    @Override
    public String toString() {
        return "if(" + predicate + ", " + left + ", " + right + ")";
    }
}
