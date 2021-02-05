package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.functions.Function;

import java.lang.reflect.Method;

public interface NativeFunction extends Function {
    Method getMethod() throws NoSuchMethodException;
}
