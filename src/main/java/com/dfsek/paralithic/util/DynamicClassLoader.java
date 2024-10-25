package com.dfsek.paralithic.util;

import com.dfsek.paralithic.Expression;


public class DynamicClassLoader extends ClassLoader {
    public DynamicClassLoader() {
        super(Expression.class.getClassLoader());
    }

    public Class<?> defineClass(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
