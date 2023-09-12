/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.function;

import io.github.antikyth.searchable.util.match.Matcher;

import java.util.Objects;

@FunctionalInterface
public interface MatcherQuadFunctionTempCache<T, U, V, R> {
	R apply(Matcher matcher, T t, U u, V v, PentaFunction<Matcher, T, U, V, R> function);

	static <T, U, V, R> MatcherQuadFunctionTempCache<T, U, V, R> create() {
		var cache = new Object() {
			Matcher matcher = null;
			T t = null;
			U u = null;
			V v = null;

			R result = null;
		};

		return (matcher, t, u, v, function) -> {
			if (!Objects.equals(matcher, cache.matcher) || !Objects.equals(t, cache.t) || !Objects.equals(u, cache.u) || !Objects.equals(v, cache.v)) {
				cache.matcher = matcher;

				cache.t = t;
				cache.u = u;
				cache.v = v;
				cache.result = function.apply(matcher, t, u, v);
			}

			return cache.result;
		};
	}
}
