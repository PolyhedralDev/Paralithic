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
        switch(op) {
            case ADD:
                return new AdditionNode(left, right);
            case SUBTRACT:
                return new SubtractionNode(left, right);
            case MULTIPLY:
                return new MultiplicationNode(left, right);
            case DIVIDE:
                return new DivisionNode(left, right);
            case POWER:
                return new NativeFunctionNode(NativeMath.POW, Arrays.asList(left, right));
            case MODULO:
                return new ModuloNode(left, right);
            case LT:
            case LT_EQ:
            case GT:
            case GT_EQ:
            case EQ:
            case NEQ:
                return new ComparisonNode(left, right, op);
            case AND:
                return new AndNode(left, right);
            case OR:
                return new OrNode(left, right);
            default:
                throw new UnsupportedOperationException(String.valueOf(op));
        }
    }
}
