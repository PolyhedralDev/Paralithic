package com.dfsek.paralithic.operations.special.function;

import com.dfsek.paralithic.functions.dynamic.Context;
import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.Simplifiable;
import com.dfsek.paralithic.operations.constant.Constant;
import com.dfsek.paralithic.operations.constant.DoubleConstant;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class FunctionNode implements Node, Simplifiable {
    private final List<Node> args;
    private final DynamicFunction function;
    private final String fName;

    private static final String DYNAMIC_FUNCTION_CLASS_NAME = DynamicFunction.class.getCanonicalName().replace('.', '/');

    private static final String CONTEXT_CLASS_NAME = Context.class.getCanonicalName().replace('.', '/');

    public FunctionNode(List<Node> args, DynamicFunction function, String fName) {
        this.args = args.stream().map(OperationUtils::simplify).collect(Collectors.toList());
        this.function = function;
        this.fName = fName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(fName).append('(');
        args.forEach(stringBuilder::append);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitVarInsn(ALOAD, 0); // Push "this" reference to top of stack
        visitor.visitFieldInsn(GETFIELD, generatedImplementationName, fName, "L" + DYNAMIC_FUNCTION_CLASS_NAME + ";"); // Push reference to field to top of stack
        visitor.visitVarInsn(ALOAD, 1); // Push context to top of stack
        OperationUtils.siPush(visitor, args.size()); // Push array size to stack
        visitor.visitIntInsn(NEWARRAY, T_DOUBLE); // Create new array with type double
        for(int i = 0; i < args.size(); i++) {
            visitor.visitInsn(DUP); // Duplicate array reference
            OperationUtils.siPush(visitor, i);
            args.get(i).apply(visitor, generatedImplementationName); // Push result of args to stack
            visitor.visitInsn(DASTORE); // Store value in array
        }
        visitor.visitMethodInsn(INVOKEINTERFACE, DYNAMIC_FUNCTION_CLASS_NAME, "eval", "(L" + CONTEXT_CLASS_NAME + ";[D)D", true); // Invoke method
    }

    @Override
    public int canSimplify() {
        if(function.isStateless() && args.stream().allMatch(op -> op instanceof Constant)) return CONSTANT_ARGUMENTS;
        return NO_SIMPLIFY;
    }

    @Override
    public Node simplify() {
        if(opCode == CONSTANT_ARGUMENTS)
            return new DoubleConstant(function.eval(args.stream().mapToDouble(op -> ((DoubleConstant) op).getValue()).toArray()));
        return this;
    }
}
