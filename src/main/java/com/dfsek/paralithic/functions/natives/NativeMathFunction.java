package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface NativeMathFunction extends NativeFunction {
    @Override
    default @NotNull Statefulness statefulness() {
        return Statefulness.STATELESS; // All native math functions are completely stateless.
    }

    @Override
    default int getArgNumber() {
        try {
            return getMethod().getParameterCount();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default NativeMathFunction withSimplifyRule(java.util.function.Function<List<Node>, Optional<Node>> rule) {
        return new NativeMathFunction() {
            @Override
            public Method getMethod() throws NoSuchMethodException {
                return NativeMathFunction.this.getMethod();
            }

            @Override
            public int getArgNumber() {
                return NativeMathFunction.this.getArgNumber();
            }

            @Override
            public NativeMathFunction withSimplifyRule(Function<List<Node>, Optional<Node>> rule) {
                return NativeMathFunction.super.withSimplifyRule(rule);
            }

            @Override
            public @NotNull Statefulness statefulness() {
                return NativeMathFunction.this.statefulness();
            }

            @Override
            public Optional<Node> simplify(List<Node> args) {
                return NativeMathFunction.super.simplify(args).or(() -> rule.apply(args));
            }
        };
    }
}
