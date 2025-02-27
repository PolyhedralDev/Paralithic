package com.dfsek.paralithic.functions.natives;

import com.dfsek.paralithic.functions.Function;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;


public interface NativeFunction extends Function {
    Method getMethod() throws NoSuchMethodException;

    @Override
    default NativeFunction withSimplifyRule(java.util.function.Function<List<Node>, Optional<Node>> rule) {
        return new NativeFunction() {
            @Override
            public Method getMethod() throws NoSuchMethodException {
                return NativeFunction.this.getMethod();
            }

            @Override
            public int getArgNumber() {
                return NativeFunction.this.getArgNumber();
            }

            @Override
            public @NotNull Statefulness statefulness() {
                return NativeFunction.this.statefulness();
            }

            @Override
            public Optional<Node> simplify(List<Node> args) {
                return NativeFunction.super.simplify(args).or(() -> rule.apply(args));
            }
        };
    }
}
