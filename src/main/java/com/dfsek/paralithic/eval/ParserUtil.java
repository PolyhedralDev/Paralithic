package com.dfsek.paralithic.eval;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.binary.*;
import com.dfsek.paralithic.operations.binary.booleans.AndNode;
import com.dfsek.paralithic.operations.binary.booleans.ComparisonNode;
import com.dfsek.paralithic.operations.binary.booleans.OrNode;
import com.dfsek.paralithic.operations.binary.number.AdditionNode;
import com.dfsek.paralithic.operations.binary.number.DivisionNode;
import com.dfsek.paralithic.operations.binary.number.ModuloNode;
import com.dfsek.paralithic.operations.binary.number.MultiplicationNode;
import com.dfsek.paralithic.operations.binary.number.SubtractionNode;
import com.dfsek.paralithic.operations.binary.special.PowerNode;

public class ParserUtil {
    public static BinaryNode createBinaryOperation(BinaryNode.Op op, Node left, Node right) {
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
                return new PowerNode(left, right);
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
