/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.match;

import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

public interface Matcher {
	/**
	 * Returns whether the given {@code query} is a valid query for the matcher.
	 * <p>
	 * By default, this always returns {@code true}, but it may be overridden e.g. for
	 * {@linkplain Matchers#REGEX the regex matcher}.
	 */
	default Optional<PatternSyntaxException> validateQueryError(String query) {
		return Optional.empty();
	}

	/**
	 * Returns whether there are any matches for the given {@code query} within the given {@code target} string.
	 */
	boolean hasMatches(String target, String query);

	/**
	 * Finds all the {@linkplain Match matches} for the given {@code query} within the given {@code target}
	 * string.
	 *
	 * @return A list of non-overlapping {@linkplain Match matches} in ascending order of indexes.
	 */
	List<Match> findMatches(String target, String query);
}
