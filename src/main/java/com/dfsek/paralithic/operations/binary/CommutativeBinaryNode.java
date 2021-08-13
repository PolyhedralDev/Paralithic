package com.dfsek.paralithic.operations.binary;

import com.dfsek.paralithic.operations.Node;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.constant.Constant;

public abstract class CommutativeBinaryNode extends BinaryNode {
    public CommutativeBinaryNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int specialSimplify() {
        if(left instanceof Constant && getClass().isInstance(right)) {
            CommutativeBinaryNode rightBin = (CommutativeBinaryNode) right;
            if(rightBin.left instanceof Constant || rightBin.right instanceof Constant) return COMMUTATIVE_RIGHT;
        }
        if(getClass().isInstance(left) && right instanceof Constant) {
            CommutativeBinaryNode leftBin = (CommutativeBinaryNode) left;
            if(leftBin.left instanceof Constant || leftBin.right instanceof Constant) return COMMUTATIVE_LEFT;
        }
        return NO_SIMPLIFY;
    }

    @Override
    public Node simplify(int opCode) {
        if(opCode == COMMUTATIVE_LEFT || opCode == COMMUTATIVE_RIGHT) return OperationUtils.simplify(merge(opCode));
        return constantSimplify();
    }

    private Node merge(int opCode) {
        if(opCode == COMMUTATIVE_LEFT) {
            CommutativeBinaryNode leftBin = (CommutativeBinaryNode) left;
            boolean cSide = leftBin.left instanceof Constant;
            Node simplified = OperationUtils.simplify(newInstance(cSide ? leftBin.left : leftBin.right, right));
            return newInstance(cSide ? leftBin.right : leftBin.left, simplified);
        }
        if(opCode == COMMUTATIVE_RIGHT) {
            CommutativeBinaryNode rightBin = (CommutativeBinaryNode) right;
            boolean cSide = rightBin.left instanceof Constant;
            Node simplified = OperationUtils.simplify(newInstance(left, cSide ? rightBin.left : rightBin.right));
            return newInstance(simplified, cSide ? rightBin.right : rightBin.left);
        }
        throw new IllegalArgumentException("Illegal opcode: " + opCode);
    }

    protected abstract BinaryNode newInstance(Node left, Node right);

    public abstract Node constantSimplify();
}
