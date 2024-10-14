/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Contains a mapping of names to constants.
 * <p>
 * Both the user as well as the {@link Parser} use a Scope to resolve a name into a {@link NamedConstant}. In
 * contrast to a simple Map, this approach provides two advantages: It's usually faster, as the constant
 * only needs to be resolved once. Reading it when evaluating an expression is as
 * cheap as a simple field access. The second advantage is that scopes can be chained. So constants can be either
 * shared by two expression or kept separate, if required.
 */
public class Scope {
    private static Scope root;
    private Scope parent;
    private final Map<String, NamedConstant> context = new ConcurrentHashMap<>();
    private final List<String> invocationVars = new ArrayList<>();

    /**
     * Creates a new empty scope.
     * <p>
     * The scope will not be completely empty, as {@link Math#PI} (pi) and {@link Math#E} (E) are always
     * defined as constants.
     */
    public Scope() {
        this(false);
    }

    private Scope(boolean skipParent) {
        if(!skipParent) {
            this.parent = getRootScope();
        }
    }

    /*
     * Creates the internal root scope which contains eternal constants ;-)
     */
    private static Scope getRootScope() {
        if(root == null) {
            synchronized(Scope.class) {
                root = new Scope(true);
                root.create("pi", Math.PI);
                root.create("euler", Math.E);
            }
        }

        return root;
    }

    /**
     * Specifies the parent scope for this scope.
     * <p>
     * If a scope cannot resolve a constant, it tries to resolve it using its parent scope. This permits to
     * share a certain set of constants.
     *
     * @param parent the parent scope to use. If {@code null}, the common root scope is used which defines a bunch of
     *               constants (e and pi).
     * @return the instance itself for fluent method calls
     */
    public Scope withParent(Scope parent) {
        if(parent == null) {
            this.parent = getRootScope();
        } else {
            this.parent = parent;
        }
        return this;
    }

    /**
     * Searches or creates a constant in this scope.
     * <p>
     * Tries to find a constant with the given name in this scope.
     *
     * @param name the constant to create
     * @return a constant with the given name from the local scope
     */
    public NamedConstant create(String name, double value) {
        NamedConstant result = new NamedConstant(name, value);
        context.put(name, result);
        return result;
    }

    /**
     * Add an invocation variable to this scope.
     *
     * @param name Identifier to give variable.
     */
    public void addInvocationVariable(String name) {
        if(invocationVars.contains(name)
                || find(name) != null)
            throw new IllegalArgumentException("constant \"" + name + "\" already defined in this scope.");
        invocationVars.add(name);
    }

    /**
     * Remove an invocation variable that matches the identifier.
     *
     * <p>
     * The scope is unchanged if there is no invocation variable matching the passed identifier.
     *
     * All invocation variables after the removed variable are shifted left by 1.
     *
     * @param name Identifier of the invoation variable.
     */
    public void removeInvocationVariable(String name) {
        invocationVars.remove(name);
    }

    public int getInvocationVarIndex(String name) {
        return invocationVars.indexOf(name);
    }

    /**
     * Searches for a {@link NamedConstant} with the given name.
     * <p>
     * If the constant does not exist {@code null}  will be returned
     *
     * @param name the name of the constant to search
     * @return the constant with the given name or {@code null} if no such constant was found
     */
    public NamedConstant find(String name) {
        if(context.containsKey(name)) {
            return context.get(name);
        }
        if(parent != null) {
            return parent.find(name);
        }
        return null;
    }

    /**
     * Removes the constant with the given name from this scope.
     * <p>
     * This does not remove the constant from a parent scope.
     *
     * @param name the name of the constant to remove
     * @return the removed constant or {@code null} if no constant with the given name existed
     */
    public NamedConstant remove(String name) {
        if(context.containsKey(name)) {
            return context.remove(name);
        } else {
            return null;
        }
    }

    /**
     * Returns all names of constants known to this scope (ignoring those of the parent scope).
     *
     * @return a set of all known constant names
     */
    public Set<String> getLocalNames() {
        return context.keySet();
    }

    /**
     * Returns all names of constants known to this scope or one of its parent scopes.
     *
     * @return a set of all known constant names
     */
    public Set<String> getNames() {
        if(parent == null) {
            return getLocalNames();
        }
        Set<String> result = new TreeSet<>();
        result.addAll(parent.getNames());
        result.addAll(getLocalNames());
        return result;
    }

    /**
     * Returns all constants known to this scope (ignoring those of the parent scope).
     *
     * @return a collection of all known constants
     */
    public Collection<NamedConstant> getLocalConstants() {
        return context.values();
    }

    /**
     * Returns all constants known to this scope or one of its parent scopes.
     *
     * @return a collection of all known constants
     */
    public Collection<NamedConstant> getConstants() {
        if(parent == null) {
            return getLocalConstants();
        }
        List<NamedConstant> result = new ArrayList<>();
        result.addAll(parent.getConstants());
        result.addAll(getLocalConstants());
        return result;
    }
}
