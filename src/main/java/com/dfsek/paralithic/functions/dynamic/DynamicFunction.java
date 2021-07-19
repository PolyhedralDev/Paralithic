package com.dfsek.paralithic.functions.dynamic;

import com.dfsek.paralithic.functions.Function;

public interface DynamicFunction extends Function {
    double eval(double... args);

    default double eval(Context context, double... args) {
        return eval(args);
    }
}
