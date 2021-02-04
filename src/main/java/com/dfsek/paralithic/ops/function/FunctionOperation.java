package com.dfsek.paralithic.ops.function;

import com.dfsek.paralithic.function.dynamic.DynamicFunction;
import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.OperationUtils;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionOperation implements Operation {
    private final List<Operation> args;
    private final String fName;

    public FunctionOperation(List<Operation> args, String fName) {
        this.args = args.stream().map(OperationUtils::simplify).collect(Collectors.toList());
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
        visitor.visitFieldInsn(GETFIELD, generatedImplementationName, fName, "L" + DynamicFunction.class.getCanonicalName().replace('.', '/') + ";"); // Push reference to field to top of stack
        visitor.visitIntInsn(SIPUSH, args.size()); // Push array size to stack
        visitor.visitIntInsn(NEWARRAY, T_DOUBLE); // Create new array with type double
        for(int i = 0; i < args.size(); i++) {
            visitor.visitInsn(DUP); // Duplicate array reference
            visitor.visitIntInsn(SIPUSH, i);
            args.get(i).apply(visitor, generatedImplementationName); // Push result of args to stack
            visitor.visitInsn(DASTORE); // Store value in array
        }
        visitor.visitMethodInsn(INVOKEINTERFACE, DynamicFunction.class.getCanonicalName().replace('.', '/'), "eval", "([D)D", true); // Invoke method
    }
}
