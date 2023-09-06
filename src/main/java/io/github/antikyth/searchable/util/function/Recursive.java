package io.github.antikyth.searchable.util.function;

import java.util.function.BiConsumer;

public class Recursive<F> {
	private F function;

	/**
	 * Recursive {@link BiConsumer}.
	 */
	public static <T, U> BiConsumer<T, U> biConsumer(RecursiveBiConsumer<T, U> function) {
		final Recursive<BiConsumer<T, U>> recursive = new Recursive<>();
		return recursive.function = (t, u) -> function.accept(t, u, recursive.function);
	}
}
