/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.parser;

/**
 * Represents a variable which binds a value to a name.
 * <p>
 * A variable is resolved or created using a {@link Scope}. This ensures that the same name always resolves to the
 * same variable. In contrast to using a Map, reading and writing a variable can be much faster, as it only needs
 * to be resolved once. Reading and writing it, is basically as cheap as a field access.
 * <p>
 * A variable can be made constant, which will fail all further attempts to change it.
 */
public class NamedConstant {

    private final String name;
    private final double value;

    /**
     * Creates a new variable.
     * <p>
     * Variables should only be created by their surrounding {@link Scope} so that all following look-ups
     * yield the same variable.
     *
     * @param name the name of the variable
     */
    protected NamedConstant(String name, double value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Returns the value previously set.
     *
     * @return the value previously set or 0 if the variable is not written yet
     */
    public double getValue() {
        return value;
    }


    @Override
    public String toString() {
        return name + ": " + value;
    }

    /**
     * Returns the name of the variable.
     *
     * @return the name of this variable
     */
    public String getName() {
        return name;
    }
}
