package com.dfsek.paralithic.functions.operation;

import com.dfsek.paralithic.functions.Function;
import com.dfsek.paralithic.node.Node;

import java.util.List;

/**
 * Function implementation that directly creates an {@link Node} to generate custom bytecode.
 */
public interface NodeFunction extends Function {
    /**
     * Generate an {@link Node} from a list of arguments.
     * @param params Arguments passed to the function.
     * @return {@link Node} representing this function.
     */
    Node createNode(List<Node> params);
}
