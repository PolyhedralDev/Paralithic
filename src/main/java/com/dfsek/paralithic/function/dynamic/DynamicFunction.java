package com.dfsek.paralithic.function.dynamic;

import com.dfsek.paralithic.function.Function;

public interface DynamicFunction extends Function {
    double eval(double... args);
    boolean isStateless();
}
