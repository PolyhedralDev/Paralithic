package com.dfsek.paralithic.node;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

public interface Node {
    /**
     * Apply this node to a {@link MethodVisitor}.
     * <p>
     * It is expected that each application leaves one double value on the stack.
     * @param visitor MethodVisitor to use
     * @param generatedImplementationName Name of the (to be) generated class
     */
    void apply(@NotNull MethodVisitor visitor, String generatedImplementationName);

    Statefulness statefulness();

    double eval(double... inputs);
}
