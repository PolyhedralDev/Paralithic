package com.dfsek.paralithic.operations;

public interface Simplifiable {
    /**
     * Do not attempt to simplify
     */
    int NO_SIMPLIFY = -1;
    /**
     * If statement with a constant predicate
     */
    int CONSTANT_PREDICATE = 0;
    /**
     * Binary expression with constant operands
     */
    int CONSTANT_OPERANDS = 1;
    /**
     * Function invocation with constant arguments
     */
    int CONSTANT_ARGUMENTS = 2;
    /**
     * Operation raised to the power of zero
     */
    int POW_0 = 3;
    /**
     * Operation raised to the power of 1
     */
    int POW_1 = 4;
    /**
     * Operation raised to the power of 2
     */
    int POW_2 = 5;
    /**
     * Left expression is a commutative operation with 1 constant, right expression is a constant.
     */
    int COMMUTATIVE_LEFT = 6;
    /**
     * Right expression is a commutative operation with 1 constant, left expression is a constant.
     */
    int COMMUTATIVE_RIGHT = 7;
    int canSimplify();
    Operation simplify(int opCode);
}
