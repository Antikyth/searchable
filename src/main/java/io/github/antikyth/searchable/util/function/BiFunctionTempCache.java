package io.github.antikyth.searchable.util.function;

import io.github.antikyth.searchable.util.Pair;

import java.util.Objects;
import java.util.function.BiFunction;


/**
 * Caches the results of a {@link BiFunction} until it is called with different arguments.
 * <p>
 * This is used with search queries, as many different queries will be applied, but the same query will rarely be
 * applied multiple times unless it is in a row for multiple function calls relating to the same query.
 */
@FunctionalInterface
public interface BiFunctionTempCache<T, U, R> {
	R apply(T t, U u, BiFunction<T, U, R> function);

	/**
	 * Caches the results of a {@link BiFunction} until it is called with different arguments.
	 * <p>
	 * This is used with search queries, as many different queries will be applied, but the same query will rarely be
	 * applied multiple times unless it is multiple function calls in a row relating to the same query.
	 */
	static <T, U, R> BiFunctionTempCache<T, U, R> create() {
		var cache = new Object() {
			Pair<T, U> args = null;
			R result = null;
		};

		return (t, u, function) -> {
			var args = new Pair<>(t, u);

			if (!Objects.equals(args, cache.args)) {
				cache.args = args;
				cache.result = function.apply(args.first, args.second);
			}

			return cache.result;
		};
	}
}
