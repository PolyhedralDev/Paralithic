package com.dfsek.paralithic.eval;

import com.dfsek.paralithic.functions.natives.NativeMath;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.binary.*;
import com.dfsek.paralithic.node.binary.booleans.AndNode;
import com.dfsek.paralithic.node.binary.booleans.ComparisonNode;
import com.dfsek.paralithic.node.binary.booleans.OrNode;
import com.dfsek.paralithic.node.binary.number.AdditionNode;
import com.dfsek.paralithic.node.binary.number.DivisionNode;
import com.dfsek.paralithic.node.binary.number.ModuloNode;
import com.dfsek.paralithic.node.binary.number.MultiplicationNode;
import com.dfsek.paralithic.node.binary.number.SubtractionNode;
import com.dfsek.paralithic.node.special.function.NativeFunctionNode;

import java.util.Arrays;

public class ParserUtil {
    public static Node createBinaryOperation(BinaryNode.Op op, Node left, Node right) {
        return switch (op) {
            case ADD -> new AdditionNode(left, right);
            case SUBTRACT -> new SubtractionNode(left, right);
            case MULTIPLY -> new MultiplicationNode(left, right);
            case DIVIDE -> new DivisionNode(left, right);
            case POWER -> new NativeFunctionNode(NativeMath.POW, Arrays.asList(left, right));
            case MODULO -> new ModuloNode(left, right);
            case LT, LT_EQ, GT, GT_EQ, EQ, NEQ -> new ComparisonNode(left, right, op);
            case AND -> new AndNode(left, right);
            case OR -> new OrNode(left, right);
        };
    }
}
