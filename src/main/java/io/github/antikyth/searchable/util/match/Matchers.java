package io.github.antikyth.searchable.util.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
}
