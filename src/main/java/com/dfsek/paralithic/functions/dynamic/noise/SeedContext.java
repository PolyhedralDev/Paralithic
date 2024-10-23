/*
 * Copyright (c) 2020-2024 Polyhedral Development
 *
 * The Terra Core Addons are licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in this module's root directory.
 */

package com.dfsek.paralithic.functions.dynamic.noise;


import com.dfsek.paralithic.functions.dynamic.Context;

public record SeedContext(long seed) implements Context {
}
