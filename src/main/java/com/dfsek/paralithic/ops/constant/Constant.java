package com.dfsek.paralithic.ops.constant;

import com.dfsek.paralithic.ops.Operation;

public abstract class Constant<T> implements Operation {
    protected final T value;

    protected Constant(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
