package com.dfsek.paralithic;

public class DynamicClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
