package com.dfsek.paralithic.function.natives;

import com.dfsek.paralithic.function.Function;

import java.lang.reflect.Method;

public interface NativeFunction extends Function {
    Method getMethod() throws NoSuchMethodException;
    boolean isStateless();
}
