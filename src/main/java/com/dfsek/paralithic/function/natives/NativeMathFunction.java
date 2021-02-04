package com.dfsek.paralithic.function.natives;

public abstract class NativeMathFunction implements NativeFunction {
    @Override
    public boolean isStateless() {
        return true;
    }

    @Override
    public int getArgNumber() {
        try {
            return getMethod().getParameterCount();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
