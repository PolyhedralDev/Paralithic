package com.dfsek.paralithic.operations.special.function;

import com.dfsek.paralithic.operations.Operation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class LocalVariableDeclarationOperation implements Operation {
    private final int lvIndex;
    private final Operation value;

    public LocalVariableDeclarationOperation(int lvIndex, Operation value) {
        this.lvIndex = lvIndex;
        this.value = value;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {

    }

    public Operation getValue() {
        return value;
    }

    public int getLvIndex() {
        return lvIndex;
    }
}
