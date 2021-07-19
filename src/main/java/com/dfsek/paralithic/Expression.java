package com.dfsek.paralithic;

import com.dfsek.paralithic.functions.dynamic.Context;

public interface Expression {
    Context DEFAULT_CONTEXT = new Context() {};
    default double evaluate(double... args) {
        return evaluate(DEFAULT_CONTEXT, args);
    }

    double evaluate(Context context, double... args);
}
