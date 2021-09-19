package com.dfsek.paralithic.operations.special;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.Simplifiable;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class TernaryIfNode implements Node, Simplifiable {
    private final Node predicate;
    private final Node left;
    private final Node right;

    public TernaryIfNode(Node predicate, Node left, Node right) {
        this.predicate = OperationUtils.simplify(predicate);
        this.left = OperationUtils.simplify(left);
        this.right = OperationUtils.simplify(right);
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
    public int canSimplify() {
        if(predicate instanceof Constant) return CONSTANT_PREDICATE;
        return NO_SIMPLIFY;
    }

    @Override
    public Node simplify() {
        return ((DoubleConstant) predicate).getValue() != 0 ? left : right;
    }
}
