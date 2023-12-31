/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util;

import java.util.Objects;

public class Pair<A, B> {
	public A first;
	public B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public int hashCode() {
		int first = this.first == null ? 0 : this.first.hashCode();
		int second = this.second == null ? 0 : this.second.hashCode();

		return first ^ second;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o instanceof @SuppressWarnings("rawtypes")Pair p) {
			return Objects.equals(this.first, p.first) && Objects.equals(this.second, p.second);
		}

		return false;
	}

	@Override
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
}
