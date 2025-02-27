package com.dfsek.paralithic.node;

import com.dfsek.paralithic.functions.dynamic.Context;


/**
 * Ranked measurement of the statefulness of a node.
 */
public enum Statefulness {
    /**
     * Completely stateless. Truly stateless nodes
     * can be evaluated immediately and replaced with constants.
     */
    STATELESS(0),
    /**
     * Requires {@link Context}, otherwise stateless.
     * Contextual nodes cannot be evaluated early,
     * as they depend on context. They can, however, be merged.
     */
    CONTEXTUAL(1),
    /**
     * Fully stateful. Stateful nodes can neither be evaluated
     * early, nor merged.
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
     *
     * @return Combined state
     */
    public static Statefulness combine(Statefulness... in) {
        Statefulness run = null;
        for(Statefulness test : in) {
            if(run == null) {
                run = test;
            } else if(test.isMoreStatefulThan(run)) {
                run = test;
            }
        }
        return run;
    }

    public static Statefulness combine(Statefulness state1, Statefulness state2) {
        return state1.isMoreStatefulThan(state2) ? state1 : state2;
    }

    public boolean isMoreStatefulThan(Statefulness test) {
        return this.rank > test.rank;
    }
}
