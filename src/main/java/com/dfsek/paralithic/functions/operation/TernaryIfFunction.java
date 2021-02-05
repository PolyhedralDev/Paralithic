package com.dfsek.paralithic.functions.operation;

import com.dfsek.paralithic.operations.Operation;
import com.dfsek.paralithic.operations.special.TernaryIfOperation;

import java.util.List;

public class TernaryIfFunction implements OperationFunction {
    @Override
    public Operation getOperation(List<Operation> params) {
        return new TernaryIfOperation(params.get(0), params.get(1), params.get(2));
    }

    @Override
    public int getArgNumber() {
        return 3;
    }

    @Override
    public boolean isStateless() {
        return true;
    }
}
