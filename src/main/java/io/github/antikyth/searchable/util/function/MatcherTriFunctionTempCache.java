/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.function;

import io.github.antikyth.searchable.util.Pair;
import io.github.antikyth.searchable.util.match.Matcher;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Objects;

@FunctionalInterface
public interface MatcherTriFunctionTempCache<T, U, R> {
	R apply(Matcher matcher, T t, U u, TriFunction<Matcher, T, U, R> function);

	static <T, U, R> MatcherTriFunctionTempCache<T, U, R> create() {
		var cache = new Object() {
			Matcher matcher = null;
			Pair<T, U> args = null;
			R result = null;
		};

		return (matcher, t, u, function) -> {
			var args = new Pair<>(t, u);

			if (!Objects.equals(matcher, cache.matcher) || !Objects.equals(args, cache.args)) {
				cache.matcher = matcher;

				cache.args = args;
				cache.result = function.apply(matcher, args.first, args.second);
			}

			return cache.result;
		};
	}
}
