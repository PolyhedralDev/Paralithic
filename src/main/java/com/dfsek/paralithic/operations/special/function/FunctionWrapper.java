package com.dfsek.paralithic.operations.special.function;

import com.dfsek.paralithic.operations.Operation;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public class FunctionWrapper implements Operation {
    private Operation delegate;

    public FunctionWrapper(Operation delegate) {
        this.delegate = delegate;
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        delegate.apply(visitor, generatedImplementationName);
    }

    public void setDelegate(Operation delegate) {
        this.delegate = delegate;
    }

    public Operation getDelegate() {
        return delegate;
    }
}
