package com.dfsek.paralithic.node.binary;

import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.NodeUtils;
import com.dfsek.paralithic.node.Constant;
import org.jetbrains.annotations.NotNull;

/**
 * Commutative binary operation. Enables advanced merging if nesting is detected.
 */
public abstract class CommutativeBinaryNode extends BinaryNode {
    public CommutativeBinaryNode(Node left, Node right) {
        super(left, right);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull Node finalSimplify() {
        if(left instanceof Constant && getClass().isInstance(right)) {
            CommutativeBinaryNode rightBin = (CommutativeBinaryNode) right;
            if(rightBin.left instanceof Constant || rightBin.right instanceof Constant) {
                boolean cSide = rightBin.left instanceof Constant;
                Node simplified = NodeUtils.simplify(newInstance(left, cSide ? rightBin.left : rightBin.right));
                return NodeUtils.simplify(newInstance(simplified, cSide ? rightBin.right : rightBin.left));
            }
        }
        if(getClass().isInstance(left) && right instanceof Constant) {
            CommutativeBinaryNode leftBin = (CommutativeBinaryNode) left;
            if(leftBin.left instanceof Constant || leftBin.right instanceof Constant) {
                boolean cSide = leftBin.left instanceof Constant;
                Node simplified = NodeUtils.simplify(newInstance(cSide ? leftBin.left : leftBin.right, right));
                return NodeUtils.simplify(newInstance(cSide ? leftBin.right : leftBin.left, simplified));
            }
        }

        return this;
    }

    protected abstract BinaryNode newInstance(Node left, Node right);
}
