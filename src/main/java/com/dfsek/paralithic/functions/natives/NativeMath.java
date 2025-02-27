package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.binary.number.DivisionNode;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;
import com.dfsek.seismic.algorithms.string.StringAlgorithms;
import com.dfsek.seismic.util.ReflectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;


public class NativeMath {
    private static final Map<String, NativeMathFunction> nativeMathFunctionTable = new TreeMap<>();

    static {
        registerMathFunctions();
    }

    public static NativeMathFunction getNativeMathFunction(String key) {
        return nativeMathFunctionTable.get(key);
    }

    public static Map<String, NativeMathFunction> getNativeMathFunctionTable() {
        return Map.copyOf(nativeMathFunctionTable);
    }

    public static void registerMathFunctions() {
        registerAllMethodsInClass(Math.class.getMethods());
        NativeMathFunction fmaFunction = nativeMathFunctionTable.get("fma");
        List<Class<?>> seismicClasses = new ArrayList<>();
        try(InputStream inputStream = Parser.class.getClassLoader().getResourceAsStream("META-INF/CLASS_MANIFEST_seismic")) {
            assert inputStream != null;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while((line = reader.readLine()) != null) {
                    if(line.startsWith("com.dfsek.seismic.math")) {
                        Class<?> clazz = ReflectionUtils.getClass(line);
                        seismicClasses.add(clazz);
                    }
                }
                for(Class<?> c : seismicClasses) {
                    registerAllMethodsInClass(c.getMethods());
                }

            }
        } catch(IOException e) {
            throw new RuntimeException("Failed to load seismic classes", e);
        }
        nativeMathFunctionTable.put("fma", fmaFunction);

        NativeMathFunction powFunction = nativeMathFunctionTable.get("pow");
        powFunction = powFunction.withSimplifyRule(args -> {
            if(args.get(1) instanceof Constant c) { // constant powers
                double v = c.getValue();
                if(v == 0) {
                    return Optional.of(Constant.of(1)); // n^0 == 1
                } else if(v == 1) {
                    return Optional.of(args.getFirst()); // n^1 == n
                } else if(v == -1) {
                    return Optional.of(new DivisionNode(Constant.of(1), args.getFirst())); // n^-1 = 1/n
                } else if(v == 0.5) {
                    return Optional.of(
                        new NativeFunctionNode(nativeMathFunctionTable.get("sqrt"), List.of(args.getFirst()))); // n^0.5 == sqrt(n)
                } else if(v == -0.5) {
                    return Optional.of(
                        new NativeFunctionNode(nativeMathFunctionTable.get("inv_sqrt"), List.of(args.getFirst()))); // n^-0.5 == 1/sqrt(n)
                } else if(v > 0 && Math.floor(v) == v) {
                    return Optional.of(new NativeFunctionNode(nativeMathFunctionTable.get("ipow"), args));
                }
            }
            if(args.get(0) instanceof Constant c) {
                double v = c.getValue();
                if(v == 0) return Optional.of(Constant.of(0)); // 0^n == 0
                else if(v == 1) return Optional.of(Constant.of(1)); // 1^n == 1
            }
            return Optional.empty();
        });
        nativeMathFunctionTable.put("pow", powFunction);
    }

    private static void registerAllMethodsInClass(Method[] methods) {
        for(Method m : methods) {
            boolean skip = false;
            for(Class<?> c2 : m.getParameterTypes()) {
                if(c2 != double.class) {
                    skip = true;
                    break;
                }
            }
            if(skip) continue;
            Class<?> returnType = m.getReturnType();
            if(returnType != double.class) continue;
            String name = StringAlgorithms.methodNameToSnakeCase(m.getName());
            NativeMathFunction function = () -> m;
            nativeMathFunctionTable.put(name, function);
        }
    }
}
