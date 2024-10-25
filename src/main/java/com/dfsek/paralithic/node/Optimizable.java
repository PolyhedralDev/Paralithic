package com.dfsek.paralithic.node;

import org.jetbrains.annotations.NotNull;


/**
 * A node that can be optimized.
 */
public interface Optimizable extends Simplifiable {
    /**
     * Optimize this node.
     * This should be preformed after simplification.
     * @return Optimized node
     */
    @NotNull
    Node optimize();
}
