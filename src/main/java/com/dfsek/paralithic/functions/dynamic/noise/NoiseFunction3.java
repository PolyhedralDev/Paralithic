/*
 * Copyright (c) 2020-2024 Polyhedral Development
 *
 * The Terra Core Addons are licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in this module's root directory.
 */

package com.dfsek.paralithic.functions.dynamic.noise;

import com.dfsek.paralithic.functions.dynamic.Context;
import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.node.Statefulness;
import com.dfsek.seismic.type.sampler.Sampler;
import org.jetbrains.annotations.NotNull;


public class NoiseFunction3 implements DynamicFunction {
    private final Sampler gen;

    public NoiseFunction3(Sampler gen) {
        this.gen = gen;
    }

    @Override
    public double eval(double... args) {
        throw new UnsupportedOperationException("Cannot evaluate seeded function without seed context.");
    }

    @Override
    public double eval(Context context, double... args) {
        return gen.getSample(((SeedContext) context).seed(), args[0], args[1], args[2]);
    }

    @Override
    public int getArgNumber() {
        return 3;
    }

    @Override
    public @NotNull Statefulness statefulness() {
        return Statefulness.CONTEXTUAL;
    }
}
