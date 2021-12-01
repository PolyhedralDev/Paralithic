package com.dfsek.paralithic.functions;

import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Function {
    /**
     * Get the number of arguments this function accepts. {@code -1} signifies a vararg function.
     * @return Number of arguments function accepts.
     */
    @Contract(pure = true)
    int getArgNumber();

    /**
     * Get whether this function is stateless. A stateless function will always return the same value for the same set
     * of parameters. Stateless functions may be evaluated early by the parser, and replaced with constants.
     * @return Whether this function is stateless.
     */
    @NotNull
    @Contract(pure = true)
    Statefulness statefulness();
}
