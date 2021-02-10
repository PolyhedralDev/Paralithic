package com.dfsek.paralithic.operations.special.function;

import com.dfsek.paralithic.operations.Operation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class LocalVariableReferenceOperation implements Operation {
    private final int localVar;

    public LocalVariableReferenceOperation(int localVar) {
        this.localVar = localVar;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        System.out.println("referencing local var: " + localVar);
        visitor.visitVarInsn(DLOAD, localVar); // Load variable to stack.
    }
}
