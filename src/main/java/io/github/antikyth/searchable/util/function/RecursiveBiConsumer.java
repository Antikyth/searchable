package io.github.antikyth.searchable.util.function;

import java.util.function.BiConsumer;

public interface RecursiveBiConsumer<T, U> {
	void accept(final T t, final U u, final BiConsumer<T, U> self);
}
