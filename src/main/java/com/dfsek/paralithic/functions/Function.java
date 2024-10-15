package com.dfsek.paralithic.functions;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.Statefulness;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface Function {
    /**
     * Get the number of arguments this function accepts. {@code -1} signifies a vararg function.
     * @return Number of arguments function accepts.
     */
    @Contract(pure = true)
    int getArgNumber();

    /**
     * Get whether this function is stateless. A stateless function will always return the same value for the same set
     * of parameters. Stateless functions may be evaluated early by the parser, and replaced with constants.
     * @return Whether this function is stateless.
     */
    @NotNull
    @Contract(pure = true)
    Statefulness statefulness();

    /**
     * Simplify this function.
     */
    default Optional<Node> simplify(List<Node> args) {
        return Optional.empty();
    }

    default Function withSimplifyRule(java.util.function.Function<List<Node>, Optional<Node>> rule) {
        return new Function() {
            @Override
            public int getArgNumber() {
                return Function.this.getArgNumber();
            }

            @Override
            public @NotNull Statefulness statefulness() {
                return Function.this.statefulness();
            }

            @Override
            public Optional<Node> simplify(List<Node> args) {
                return Function.super.simplify(args).or(() -> rule.apply(args));
            }
        };
    }
}
