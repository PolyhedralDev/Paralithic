package com.dfsek.paralithic.ops.function;

import com.dfsek.paralithic.function.natives.NativeFunction;
import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.OperationUtils;
import com.dfsek.paralithic.ops.Simplifiable;
import com.dfsek.paralithic.ops.constant.Constant;
import com.dfsek.paralithic.ops.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class NativeFunctionOperation implements Operation, Simplifiable {
    private final NativeFunction function;
    private final List<Operation> args;

    public NativeFunctionOperation(NativeFunction function, List<Operation> args) {
        this.function = function;
        this.args = args.stream().map(OperationUtils::simplify).collect(Collectors.toList());
    }

    public boolean canSimplify() {
        return args.stream().allMatch(op -> op instanceof Constant)
                && function.isStateless(); // Only simplify stateless functions
    }

    public Operation simplify() {
        Object[] arg = args.stream().mapToDouble(op -> ((DoubleConstant) op).getValue()).boxed().toArray();
        try {
            return new DoubleConstant((Double) function.getMethod().invoke(null, arg));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

        if(args.size() != params.length) throw new IllegalArgumentException("Arguments do not match. Expected " + params.length + ", found " + args.size());

        StringBuilder signature = new StringBuilder("(");

        for(int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            args.get(i).apply(visitor, generatedImplementationName); // Push result of args to stack
            if(OperationUtils.isWeakInteger(type)) visitor.visitInsn(I2D);
            else if(OperationUtils.isFloat(type)) visitor.visitInsn(F2D);
            else if(OperationUtils.isLong(type)) visitor.visitInsn(L2D);
            else if(!OperationUtils.isDouble(type)) throw new IllegalArgumentException("Illegal parameter type: " + params[i]);
            signature.append(OperationUtils.getDescriptorCharacter(type));
        }
        signature.append(')');

        int castInsn = Integer.MIN_VALUE;
        Class<?> type = nativeMethod.getReturnType();

        if(OperationUtils.isWeakInteger(type)) castInsn = I2D;
        else if(OperationUtils.isFloat(type)) castInsn = F2D;
        else if(OperationUtils.isLong(type)) castInsn = L2D;
        else if(!OperationUtils.isDouble(type)) throw new IllegalArgumentException("Illegal return type: " + type);

        signature.append(OperationUtils.getDescriptorCharacter(nativeMethod.getReturnType()));

        visitor.visitMethodInsn(INVOKESTATIC, nativeMethod.getDeclaringClass().getCanonicalName().replace('.', '/'), nativeMethod.getName(), signature.toString(), false); // Invoke method

        if(castInsn != Integer.MIN_VALUE) visitor.visitInsn(castInsn);
    }
}
