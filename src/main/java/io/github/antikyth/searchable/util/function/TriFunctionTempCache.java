/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.function;

import org.apache.commons.lang3.function.TriFunction;

import java.util.Objects;
import java.util.function.BiFunction;


/**
 * Caches the results of a {@link BiFunction} until it is called with different arguments.
 * <p>
 * This is used with search queries, as many different queries will be applied, but the same query will rarely be
 * applied multiple times unless it is in a row for multiple function calls relating to the same query.
 */
@FunctionalInterface
public interface TriFunctionTempCache<T, U, V, R> {
	R apply(T t, U u, V v, TriFunction<T, U, V, R> function);

	/**
	 * Caches the results of a {@link BiFunction} until it is called with different arguments.
	 * <p>
	 * This is used with search queries, as many different queries will be applied, but the same query will rarely be
	 * applied multiple times unless it is multiple function calls in a row relating to the same query.
	 */
	static <T, U, V, R> TriFunctionTempCache<T, U, V, R> create() {
		var cache = new Object() {
			T t;
			U u;
			V v;

			R result = null;
		};

		return (t, u, v, function) -> {
			if (!Objects.equals(t, cache.t) || !Objects.equals(u, cache.u) || !Objects.equals(v, cache.v)) {
				cache.t = t;
				cache.u = u;
				cache.v = v;

				cache.result = function.apply(t, u, v);
			}

			return cache.result;
		};
	}
}
