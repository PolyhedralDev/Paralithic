package com.dfsek.paralithic.node.special;

import com.dfsek.paralithic.node.*;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class TernaryIfNode implements Simplifiable {
    private Node predicate;
    private Node left;
    private Node right;

    private final Lazy<Statefulness> statefulness = Lazy.of(() -> Statefulness.combine(predicate.statefulness(), left.statefulness(), right.statefulness())); // Cache statefulness.


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
    public Statefulness statefulness() {
        return statefulness.get();
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
        return this;
    }

    @Override
    public String toString() {
        return "if(" + predicate + ", " + left + ", " + right + ")";
    }
}
