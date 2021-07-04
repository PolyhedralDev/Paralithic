package com.dfsek.paralithic.functions.natives;

public interface NativeMathFunction extends NativeFunction {
    @Override
    default boolean isStateless() {
        return true;
    }

    @Override
    default int getArgNumber() {
        try {
            return getMethod().getParameterCount();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
