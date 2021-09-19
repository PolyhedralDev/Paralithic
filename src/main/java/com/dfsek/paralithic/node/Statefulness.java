package com.dfsek.paralithic.node;

/**
 * Ranked measurement of the statefulness of a node.
 */
public enum Statefulness {
    /**
     * Completely stateless.
     */
    STATELESS(0),
    /**
     * Requires context, otherwise stateless.
     */
    CONTEXT(1),
    /**
     * Stateful.
     */
    STATEFUL(2);

    private final int rank;

    Statefulness(int rank) {
        this.rank = rank;
    }

    /**
     * Combine statefulness measures. Stateful overrides stateless.
     *
     * @param in States
     * @return Combined state
     */
    public static Statefulness combine(Statefulness... in) {
        Statefulness run = null;
        for (Statefulness test : in) {
            if (run == null) {
                run = test;
            } else if (test.isMoreStatefulThan(run)) {
                run = test;
            }
        }
        return run;
    }

    public boolean isMoreStatefulThan(Statefulness test) {
        return this.rank > test.rank;
    }
}
