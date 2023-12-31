/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.match;

import io.github.antikyth.searchable.Searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Matchers {
	public static final Matcher PLAIN = new Matcher() {
		@Override
		public boolean hasMatches(String target, String query) {
			if (query == null || target == null || target.length() < query.length()) return false;
			if (query.isEmpty()) return true;

			String lowercaseTarget = target.toLowerCase(Locale.ROOT);
			String lowercaseQuery = query.toLowerCase(Locale.ROOT);

			return lowercaseTarget.contains(lowercaseQuery);
		}

		@Override
		public List<Match> findMatches(String target, String query) {
			if (query == null || query.isEmpty() || target == null || target.length() < query.length())
				return List.of();

			List<Match> matches = new ArrayList<>();

			String lowercaseTarget = target.toLowerCase(Locale.ROOT);
			String lowercaseQuery = query.toLowerCase(Locale.ROOT);

			int currentIndex = 0;
			while (currentIndex >= 0) {
				currentIndex = lowercaseTarget.indexOf(lowercaseQuery, currentIndex);

				if (currentIndex >= 0) {
					matches.add(new Match(currentIndex, currentIndex + query.length()));
					currentIndex += lowercaseQuery.length();
				}
			}

			return matches;
		}
	};

	public static final Matcher REGEX = new Matcher() {
		private String query;
		private java.util.regex.Matcher matcher;

		/**
		 * Updates the matcher for the given {@code target} and {@code query}.
		 * @return Whether the pattern was able to compile successfully.
		 */
		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		private boolean updateMatcher(String target, String query) {
			if (query == null) return false;

			if (this.matcher == null || !query.equals(this.query)) {
				Pattern pattern;
				try {
					pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
				} catch (PatternSyntaxException exception) {
					Searchable.LOGGER.debug("regex query \"" + query + "\" failed to compile:\n" + exception.getMessage());

					return false;
				}

				this.matcher = pattern.matcher(target);

				this.query = query;
			} else {
				this.matcher.reset(target);
			}

			return true;
		}

		@Override
		public PatternSyntaxException validateQueryError(String query) {
			try {
				Pattern.compile(query);
			} catch (PatternSyntaxException exception) {
				return exception;
			}

			return null;
		}

		@Override
		public boolean hasMatches(String target, String query) {
			if (query == null || query.isEmpty() || target == null) return true;
			if (!updateMatcher(target, query)) return false;

			return this.matcher.find();
		}

		@Override
		public List<Match> findMatches(String target, String query) {
			if (query == null || query.isEmpty() || target == null || !updateMatcher(target, query)) {
				return List.of();
			}

			List<Match> matches = new ArrayList<>();

			while (this.matcher.find()) {
				matches.add(new Match(this.matcher.start(), this.matcher.end()));
			}

			return matches;
		}
	};
}
