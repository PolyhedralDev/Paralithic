package com.dfsek.paralithic.functions.dynamic;

import com.dfsek.paralithic.functions.Function;

public interface DynamicFunction extends Function {
    double eval(double... args);
}
