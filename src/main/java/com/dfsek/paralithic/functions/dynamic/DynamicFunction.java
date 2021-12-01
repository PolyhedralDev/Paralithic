package com.dfsek.paralithic.functions.dynamic;

import com.dfsek.paralithic.functions.Function;
import org.jetbrains.annotations.Nullable;

public interface DynamicFunction extends Function {
    double eval(double... args);

    default double eval(@Nullable Context context, double... args) {
        return eval(args);
    }
}
