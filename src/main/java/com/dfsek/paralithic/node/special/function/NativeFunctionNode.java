package com.dfsek.paralithic.node.special.function;

import com.dfsek.paralithic.functions.natives.NativeFunction;
import com.dfsek.paralithic.node.Constant;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Optimizable;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.I2D;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.L2D;


public class NativeFunctionNode implements Optimizable {
    private final NativeFunction function;
    private final List<Node> args;

    private final Lazy<Statefulness> statefulness;

    public NativeFunctionNode(NativeFunction function, List<Node> args) {
        this.function = function;
        this.args = args;
        statefulness = Lazy.of(
            () -> Statefulness.combine(args.stream().map(Node::statefulness).toArray(Statefulness[]::new))); // Cache statefulness.
    }

    public NativeFunction getFunction() {
        return function;
    }

    public List<Node> getArgs() {
        return args;
    }

    public @NotNull Node simplify() {
        List<Node> args = this.args.stream().map(NodeUtils::simplify).collect(Collectors.toList());
        statefulness.invalidate();
        if(args.stream().allMatch(op -> op instanceof Constant)) {
            Object[] arg = args.stream().mapToDouble(op -> ((Constant) op).getValue()).boxed().toArray();
            try {
                return Constant.of(((Number) function.getMethod().invoke(null, arg)).doubleValue());
            } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return function.simplify(args).orElseGet(() -> new NativeFunctionNode(function, args));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder;
        try {
            stringBuilder = new StringBuilder(function.getMethod().getName()).append('(');
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        for(int i = 0; i < args.size(); i++) {
            stringBuilder.append(args.get(i));
            if(i != args.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        Method nativeMethod;
        try {
            nativeMethod = function.getMethod();
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("Unable to fetch native method.", e);
        }

        Parameter[] params = nativeMethod.getParameters();

        if(args.size() != params.length)
            throw new IllegalArgumentException("Arguments do not match. Expected " + params.length + ", found " + args.size());

        StringBuilder signature = new StringBuilder("(");

        for(int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            args.get(i).apply(visitor, generatedImplementationName); // Push result of args to stack
            if(NodeUtils.isWeakInteger(type)) visitor.visitInsn(I2D);
            else if(NodeUtils.isFloat(type)) visitor.visitInsn(F2D);
            else if(NodeUtils.isLong(type)) visitor.visitInsn(L2D);
            else if(!NodeUtils.isDouble(type))
                throw new IllegalArgumentException("Illegal parameter type: " + params[i]);
            signature.append(NodeUtils.getDescriptorCharacter(type));
        }
        signature.append(')');

        int castInsn = Integer.MIN_VALUE;
        Class<?> type = nativeMethod.getReturnType();

        if(NodeUtils.isWeakInteger(type)) castInsn = I2D;
        else if(NodeUtils.isFloat(type)) castInsn = F2D;
        else if(NodeUtils.isLong(type)) castInsn = L2D;
        else if(!NodeUtils.isDouble(type)) throw new IllegalArgumentException("Illegal return type: " + type);

        signature.append(NodeUtils.getDescriptorCharacter(nativeMethod.getReturnType()));

        visitor.visitMethodInsn(INVOKESTATIC, nativeMethod.getDeclaringClass().getCanonicalName().replace('.', '/'), nativeMethod.getName(),
            signature.toString(), false); // Invoke method

        if(castInsn != Integer.MIN_VALUE) visitor.visitInsn(castInsn);
    }

    @Override
    public @NotNull Statefulness statefulness() {
        return statefulness.get();
    }

    @Override
    public double eval(double[] localVariables, double... inputs) {
        try {
            return (double) function.getMethod().invoke(null, args.stream().map(a -> a.eval(localVariables, inputs)).toArray());
        } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Node optimize() {
        return new NativeFunctionNode(function, args.stream().map(NodeUtils::optimize).collect(Collectors.toList()));
    }
}
