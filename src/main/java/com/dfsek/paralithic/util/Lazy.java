package com.dfsek.paralithic.util;

import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> supplier;

    private T value;

    private boolean computed = false; // Separate variable to store state to account for null value.

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Get the value. Computes the value if not present.
     * @return The value
     */
    public T get() {
        if(!computed) {
            computed = true;
            value = supplier.get();
        }
        return value;
    }

    /**
     * Invalidate contents. Contents will be recomputed upon next {@link #get()}.
     */
    public void invalidate() {
        computed = false;
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }
}
