package com.dfsek.paralithic.operations.special.function;

import com.dfsek.paralithic.operations.Operation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class LocalVariableDeclarationOperation implements Operation {
    private final int lvIndex;
    private final Operation value;

    public LocalVariableDeclarationOperation(int lvIndex, Operation value) {
        this.lvIndex = lvIndex;
        this.value = value;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        System.out.println("creating local var: " + lvIndex);
        value.apply(visitor, generatedImplementationName);
        visitor.visitInsn(DUP2); // Duplicate value on stack
        visitor.visitVarInsn(DSTORE, lvIndex); // store to local variable
    }

    public Operation getValue() {
        return value;
    }

    public int getLvIndex() {
        return lvIndex;
    }
}
