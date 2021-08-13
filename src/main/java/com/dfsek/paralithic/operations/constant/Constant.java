package com.dfsek.paralithic.operations.constant;

import com.dfsek.paralithic.operations.Node;

public abstract class Constant<T> implements Node {
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
