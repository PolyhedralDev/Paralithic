package com.dfsek.paralithic.functions.operation;

import com.dfsek.paralithic.functions.Function;
import com.dfsek.paralithic.operations.Operation;

import java.util.List;

/**
 * Function implementation that directly creates an {@link Operation} to generate custom bytecode.
 */
public interface OperationFunction extends Function {
    /**
     * Generate an {@link Operation} from a list of arguments.
     * @param params Arguments passed to the function.
     * @return {@link Operation} representing this function.
     */
    Operation getOperation(List<Operation> params);
}
