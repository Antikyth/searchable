package io.github.antikyth.searchable.util.match;

import java.util.List;

public interface Matcher {
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
