package com.dfsek.paralithic.ops;

public interface Simplifiable {
    int NO_SIMPLIFY = -1;
    int CONSTANT_PREDICATE = 0;
    int CONSTANT_OPERANDS = 1;
    int CONSTANT_ARGUMENTS = 2;
    int POW_0 = 3;
    int POW_1 = 4;
    int POW_2 = 5;
    int canSimplify();
    Operation simplify(int opCode);
}
