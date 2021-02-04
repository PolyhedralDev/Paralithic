package com.dfsek.paralithic.ops;

public interface Simplifiable {
    boolean canSimplify();
    Operation simplify();
}
