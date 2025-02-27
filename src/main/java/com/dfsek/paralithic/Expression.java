package com.dfsek.paralithic;

import com.dfsek.paralithic.functions.dynamic.Context;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;


public interface Expression {
    Context DEFAULT_CONTEXT = new Context() {
    };

    default double evaluate(double... args) {
        return evaluate(DEFAULT_CONTEXT, args);
    }

    @Contract(pure = true)
    double evaluate(@Nullable Context context, double... args);
}
