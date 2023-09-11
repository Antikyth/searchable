/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
