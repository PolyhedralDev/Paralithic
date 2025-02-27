package com.dfsek.paralithic.functions.node;

import com.dfsek.paralithic.functions.Function;
import com.dfsek.paralithic.node.Node;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Function implementation that directly creates an {@link Node} to generate custom bytecode.
 */
public interface NodeFunction extends Function {
    /**
     * Generate an {@link Node} from a list of arguments.
     *
     * @param params Arguments passed to the function.
     *
     * @return {@link Node} representing this function.
     */
    @NotNull
    @Contract("_ -> new")
    Node createNode(@NotNull List<Node> params);
}
