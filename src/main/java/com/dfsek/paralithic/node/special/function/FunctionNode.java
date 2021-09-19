package com.dfsek.paralithic.node.special.function;

import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.node.*;
import com.dfsek.paralithic.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.stream.Collectors;

import static com.dfsek.paralithic.eval.ExpressionBuilder.CONTEXT_CLASS_NAME;
import static com.dfsek.paralithic.eval.ExpressionBuilder.DYNAMIC_FUNCTION_CLASS_NAME;
import static org.objectweb.asm.Opcodes.*;

public class FunctionNode implements Simplifiable {
    private List<Node> args;
    private final DynamicFunction function;
    private final String fName;

    private final Lazy<Statefulness> statefulness = Lazy.of(() -> Statefulness.combine(args.stream().map(Node::statefulness).toArray(Statefulness[]::new))); // Cache statefulness.


    public FunctionNode(List<Node> args, DynamicFunction function, String fName) {
        this.args = args;
        this.function = function;
        this.fName = fName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(fName).append('(');
        for (int i = 0; i < args.size(); i++) {
            stringBuilder.append(args.get(i));
            if (i != args.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    @Override
    public void apply(@NotNull MethodVisitor visitor, String generatedImplementationName) {
        visitor.visitVarInsn(ALOAD, 0); // Push "this" reference to top of stack
        visitor.visitFieldInsn(GETFIELD, generatedImplementationName, fName, "L" + DYNAMIC_FUNCTION_CLASS_NAME + ";"); // Push reference to field to top of stack
        visitor.visitVarInsn(ALOAD, 1); // Push context to top of stack
        NodeUtils.siPush(visitor, args.size()); // Push array size to stack
        visitor.visitIntInsn(NEWARRAY, T_DOUBLE); // Create new array with type double
        for (int i = 0; i < args.size(); i++) {
            visitor.visitInsn(DUP); // Duplicate array reference
            NodeUtils.siPush(visitor, i);
            args.get(i).apply(visitor, generatedImplementationName); // Push result of args to stack
            visitor.visitInsn(DASTORE); // Store value in array
        }
        visitor.visitMethodInsn(INVOKEINTERFACE, DYNAMIC_FUNCTION_CLASS_NAME, "eval", "(L" + CONTEXT_CLASS_NAME + ";[D)D", true); // Invoke method
    }

    @Override
    public Statefulness statefulness() {
        if(function.isStateless()) {
            return statefulness.get();
        } else {
            return Statefulness.STATEFUL; // The function itself is stateful.
        }
    }

    @Override
    public @NotNull Node simplify() {
        this.args = args.stream().map(NodeUtils::simplify).collect(Collectors.toList());
        statefulness.invalidate();
        if (args.stream().allMatch(op -> op instanceof Constant)
                && function.isStateless()) {
            return Constant.of(function.eval(args.stream().mapToDouble(op -> ((Constant) op).getValue()).toArray()));
        }
        return this;
    }
}
