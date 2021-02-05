/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.parser;

/**
 * Represents a constant which binds a value to a name.
 * <p>
 * A constant is resolved or created using a {@link Scope}. This ensures that the same name always resolves to the
 * same variable.
 */
public class NamedConstant {

    private final String name;
    private final double value;

    /**
     * Creates a new constant.
     * <p>
     * Constants should only be created by their surrounding {@link Scope} so that all following look-ups
     * yield the same variable.
     *
     * @param name the name of the variable
     */
    protected NamedConstant(String name, double value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Returns the value of this constant.
     */
    public double getValue() {
        return value;
    }


    @Override
    public String toString() {
        return name + ": " + value;
    }
}
