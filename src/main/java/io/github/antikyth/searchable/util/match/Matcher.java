package io.github.antikyth.searchable.util.match;

import java.util.List;

public interface Matcher {
	/**
	 * Returns whether the given {@code query} is a valid query for the matcher.
	 * <p>
	 * By default, this always returns {@code true}, but it may be overridden e.g. for
	 * {@linkplain Matchers#REGEX the regex matcher}.
	 */
	default boolean validateQuery(String query) {
		return true;
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
