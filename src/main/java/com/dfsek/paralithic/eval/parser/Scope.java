/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.parser;

import net.jafama.FastMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Contains a mapping of names to variables.
 * <p>
 * Both the user as well as the {@link Parser} use a Scope to resolve a name into a {@link NamedConstant}. In
 * contrast to a simple Map, this approach provides two advantages: It's usually faster, as the variable
 * only needs to be resolved once. Modifying it and especially reading it when evaluating an expression is as
 * cheap as a simple field access. The second advantage is that scopes can be chained. So variables can be either
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
     * The scope will not be completely empty, as {@link FastMath#PI} (pi) and {@link FastMath#E} (E) are always
     * defined as constants.
     * <p>
     * If an not yet known variable is accessed, it will be created and initialized with 0.
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
                root.create("pi", FastMath.PI);
                root.create("euler", FastMath.E);
            }
        }

        return root;
    }

    /**
     * Specifies the parent scope for this scope.
     * <p>
     * If a scope cannot resolve a variable, it tries to resolve it using its parent scope. This permits to
     * share a certain set of variables.
     *
     * @param parent the parent scope to use. If <tt>null</tt>, the common root scope is used which defines a bunch of
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
     * Searches or creates a variable in this scope.
     * <p>
     * Tries to find a variable with the given name in this scope. If no variable with the given name is found,
     * the parent scope is not checked, but a new variable is created.
     *
     * @param name the variable to search or create
     * @return a variable with the given name from the local scope
     */
    public NamedConstant create(String name, double value) {
        if(context.containsKey(name)) {
            return context.get(name);
        }
        NamedConstant result = new NamedConstant(name, value);
        context.put(name, result);

        return result;
    }

    public void addInvocationVariable(String name) {
        if(invocationVars.contains(name)
                || find(name) != null)
            throw new IllegalArgumentException("Variable \"" + name + "\" already defined in this scope.");
        invocationVars.add(name);
    }

    public int getInvocationVarIndex(String name) {
        return invocationVars.indexOf(name);
    }

    /**
     * Searches for a {@link NamedConstant} with the given name.
     * <p>
     * If the variable does not exist <tt>null</tt>  will be returned
     *
     * @param name the name of the variable to search
     * @return the variable with the given name or <tt>null</tt> if no such variable was found
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
     * Removes the variable with the given name from this scope.
     * <p>
     * If will not remove the variable from a parent scope.
     *
     * @param name the name of the variable to remove
     * @return the removed variable or <tt>null</tt> if no variable with the given name existed
     */
    public NamedConstant remove(String name) {
        if(context.containsKey(name)) {
            return context.remove(name);
        } else {
            return null;
        }
    }

    /**
     * Returns all names of variables known to this scope (ignoring those of the parent scope).
     *
     * @return a set of all known variable names
     */
    public Set<String> getLocalNames() {
        return context.keySet();
    }

    /**
     * Returns all names of variables known to this scope or one of its parent scopes.
     *
     * @return a set of all known variable names
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
     * Returns all variables known to this scope (ignoring those of the parent scope).
     *
     * @return a collection of all known variables
     */
    public Collection<NamedConstant> getLocalVariables() {
        return context.values();
    }

    /**
     * Returns all variables known to this scope or one of its parent scopes.
     *
     * @return a collection of all known variables
     */
    public Collection<NamedConstant> getVariables() {
        if(parent == null) {
            return getLocalVariables();
        }
        List<NamedConstant> result = new ArrayList<>();
        result.addAll(parent.getVariables());
        result.addAll(getLocalVariables());
        return result;
    }
}
