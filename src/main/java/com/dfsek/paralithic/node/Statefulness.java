package com.dfsek.paralithic.node;

public enum Statefulness {
    /**
     * Completely stateless.
     */
    STATELESS,
    /**
     * Requires context, otherwise stateless.
     */
    CONTEXT,
    /**
     * Stateful.
     */
    STATEFUL,
}
