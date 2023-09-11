/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.function;

import java.util.function.BiConsumer;

public interface RecursiveBiConsumer<T, U> {
	void accept(final T t, final U u, final BiConsumer<T, U> self);
}
