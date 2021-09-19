package com.dfsek.paralithic.node;

import org.jetbrains.annotations.NotNull;

/**
 * A node that can be simplified.
 */
public interface Simplifiable extends Node {
    /**
     * Simplify this node
     * @return Simplified node
     */
    @NotNull
    Node simplify();
}
