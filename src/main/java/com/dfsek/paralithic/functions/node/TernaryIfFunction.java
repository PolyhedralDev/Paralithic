package com.dfsek.paralithic.functions.node;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.paralithic.node.special.TernaryIfNode;

import java.util.List;

public class TernaryIfFunction implements NodeFunction {
    @Override
    public Node createNode(List<Node> params) {
        return new TernaryIfNode(params.get(0), params.get(1), params.get(2));
    }

    @Override
    public int getArgNumber() {
        return 3;
    }

    @Override
    public Statefulness statefulness() {
        return Statefulness.STATELESS; // If function is stateless.
    }
}
