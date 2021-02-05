package com.dfsek.paralithic.operations.special;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.OperationUtils;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class InvocationVariableOperation implements Operation {
    private final int index;

    public InvocationVariableOperation(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "{" + index + "}";
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitVarInsn(ALOAD, 1); // Load array ref (ref 0 is "this" ref, 1 is first argument, which is array)
        OperationUtils.siPush(visitor, index); // Push index to stack
        visitor.visitInsn(DALOAD); // Pop index; push value to stack
    }
}
