package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;

public interface NativeMathFunction extends NativeFunction {
    @Override
    default @NotNull Statefulness statefulness() {
        return Statefulness.STATELESS; // All native math functions are completely stateless.
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
