package com.dfsek.paralithic.operations.binary;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.OperationUtils;
import com.dfsek.paralithic.operations.constant.Constant;

public abstract class CommutativeBinaryOperation extends BinaryOperation {
    public CommutativeBinaryOperation(Operation left, Operation right) {
        super(left, right);
    }

    @Override
    public int specialSimplify() {
        if(left instanceof Constant && getClass().isInstance(right)) {
            CommutativeBinaryOperation rightBin = (CommutativeBinaryOperation) right;
            if(rightBin.left instanceof Constant || rightBin.right instanceof Constant) return COMMUTATIVE_RIGHT;
        }
        if(getClass().isInstance(left) && right instanceof Constant) {
            CommutativeBinaryOperation leftBin = (CommutativeBinaryOperation) left;
            if(leftBin.left instanceof Constant || leftBin.right instanceof Constant) return COMMUTATIVE_LEFT;
        }
        return NO_SIMPLIFY;
    }

    @Override
    public Operation simplify(int opCode) {
        if(opCode == COMMUTATIVE_LEFT || opCode == COMMUTATIVE_RIGHT) return OperationUtils.simplify(merge(opCode));
        return constantSimplify();
    }

    private Operation merge(int opCode) {
        if(opCode == COMMUTATIVE_LEFT) {
            CommutativeBinaryOperation leftBin = (CommutativeBinaryOperation) left;
            boolean cSide = leftBin.left instanceof Constant;
            Operation simplified = OperationUtils.simplify(newInstance(cSide ? leftBin.left : leftBin.right, right));
            return newInstance(cSide ? leftBin.right : leftBin.left, simplified);
        }
        if(opCode == COMMUTATIVE_RIGHT) {
            CommutativeBinaryOperation rightBin = (CommutativeBinaryOperation) right;
            boolean cSide = rightBin.left instanceof Constant;
            Operation simplified = OperationUtils.simplify(newInstance(left, cSide ? rightBin.left : rightBin.right));
            return newInstance(simplified, cSide ? rightBin.right : rightBin.left);
        }
        throw new IllegalArgumentException("Illegal opcode: " + opCode);
    }

    protected abstract BinaryOperation newInstance(Operation left, Operation right);

    public abstract Operation constantSimplify();
}
