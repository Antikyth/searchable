/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.function;

import org.apache.commons.lang3.function.TriFunction;

public interface RecursiveTriFunction<T, U, V, R> {
	R accept(final T t, final U u, final V v, final TriFunction<T, U, V, R> self);
}
