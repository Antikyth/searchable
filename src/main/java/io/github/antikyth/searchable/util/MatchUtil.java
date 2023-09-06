package io.github.antikyth.searchable.util;

import io.github.antikyth.searchable.util.function.BiFunctionTempCache;
import io.github.antikyth.searchable.util.function.Recursive;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.component.TextComponent;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static io.github.antikyth.searchable.util.Util.parseLegacyText;

public class MatchUtil {
	/**
	 * A query match within a string.
	 *
	 * @param startIndex The index that the match starts at.
	 * @param endIndex   The index that the match ends at (i.e. {@code startIndex + query.length()}).
	 */
	public record Match(int startIndex, int endIndex) {
	}

	static Style highlight(Style style) {
		return style.withFormatting(Formatting.UNDERLINE, Formatting.WHITE);
	}

	/**
	 * Gets all the {@linkplain Match matches} for the given {@code query} within the given {@code target} text.
	 *
	 * @return A list of non-overlapping {@linkplain Match matches} in ascending order of indexes.
	 * @see MatchUtil#getMatches(String, String)
	 */
	public static List<Match> getMatches(StringVisitable target, String query) {
		return VISITABLE_MATCHES_CACHE.apply(target, query, (_target, _query) -> {
			var stripped = Formatting.strip(_target.getString());
			if (stripped == null) return null;

			return getMatches(stripped, _query);
		});
	}

	/**
	 * Gets all the {@linkplain Match matches} for the given {@code query} within the given {@code target} string.
	 *
	 * @return A list of non-overlapping {@linkplain Match matches} in ascending order of indexes.
	 * @see MatchUtil#getMatches(StringVisitable, String)
	 */
	public static List<Match> getMatches(String target, String query) {
		return MATCHES_CACHE.apply(target, query, MatchUtil::findMatches);
	}

	/**
	 * Returns whether there are any matches for the given {@code query} within the given {@code target} text.
	 *
	 * @see MatchUtil#hasMatches(String, String)
	 */
	public static boolean hasMatches(StringVisitable target, String query) {
		return VISITABLE_HAS_MATCHES_CACHE.apply(target, query, (_target, _query) -> {
			var stripped = Formatting.strip(_target.getString());
			if (stripped == null) return false;

			return hasMatches(stripped, _query);
		});
	}

	/**
	 * Returns whether there are any matches for the given {@code query} within the given {@code target} string.
	 *
	 * @see MatchUtil#hasMatches(StringVisitable, String)
	 */
	public static boolean hasMatches(String target, String query) {
		return HAS_MATCHES_CACHE.apply(target, query, MatchUtil::calcHasMatches);
	}

	/**
	 * Highlights the {@linkplain Match matches} for the given {@code query} within the given {@code target} text.
	 *
	 * @param target The target text which is to be highlighted.
	 * @param query  The query for which {@linkplain Match matches} are found using
	 * @return The newly highlighted text.
	 * @see MatchUtil#getHighlightedText(StringVisitable, String, BiFunction)
	 * @see MatchUtil#getHighlightedText(StringVisitable, List)
	 */
	public static StringVisitable getHighlightedText(StringVisitable target, String query) {
		return getHighlightedText(target, query, MatchUtil::getMatches);
	}

	/**
	 * Highlights the {@linkplain Match matches}, found using the given {@code matchesFactory} function, for the given
	 * {@code query} within the given {@code target} text.
	 *
	 * @param target The target text which is to be highlighted.
	 * @param query  The query for which {@linkplain Match matches} are found.
	 * @return The newly highlighted text.
	 */
	public static StringVisitable getHighlightedText(StringVisitable target, String query, BiFunction<StringVisitable, String, List<Match>> matchesFactory) {
		return getHighlightedText(target, matchesFactory.apply(target, query));
	}

	/**
	 * Highlights the given {@code matches} within the given {@code target} text.
	 *
	 * @param target  The target text which is to be highlighted.
	 * @param matches The matches to highlight. Must be sorted in ascending order. Must be non-overlapping.
	 * @return The newly highlighted text.
	 */
	public static StringVisitable getHighlightedText(StringVisitable target, List<Match> matches) {
		return HIGHLIGHT_CACHE.apply(target, matches, MatchUtil::highlightMatches);
	}

	private static boolean calcHasMatches(String target, String query) {
		if (query == null || target == null || target.length() < query.length()) return false;
		if (query.isEmpty()) return true;

		String lowercaseTarget = target.toLowerCase(Locale.ROOT);
		String lowercaseQuery = query.toLowerCase(Locale.ROOT);

		return lowercaseTarget.contains(lowercaseQuery);
	}

	private static List<Match> findMatches(String target, String query) {
		if (query == null || query.isEmpty() || target == null || target.length() < query.length()) return List.of();

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

	private static StringVisitable highlightMatches(StringVisitable target, List<Match> matches) {
		if (target == null || matches.isEmpty()) return target;

		List<Text> texts = new ArrayList<>();
		// Non-final variables are not allowed within the lambda below, so a wrapper must be made around the index, as
		// it must be reassigned to be mutated.
		var counter = new Object() {
			int index = 0;
			int currentMatch = 0;
		};

		BiConsumer<String, Style> addText = Recursive.biConsumer((string, style, self) -> {
			// If all matches have been added already.
			if (counter.currentMatch < 0) {
				texts.add(literal(string, style));
				return;
			}

			Match currentMatch = matches.get(counter.currentMatch);

			var start = currentMatch.startIndex() - counter.index;
			var end = currentMatch.endIndex() - counter.index;

			if (start <= 0 && end > 0) {
				// a match highlight is ongoing

				if (end < string.length()) {
					// match highlight ends in this string

					String substring = string.substring(0, end);

					texts.add(highlighted(substring, style));
					counter.index += substring.length();

					// Target the next match.
					int nextMatch = counter.currentMatch + 1;
					counter.currentMatch = nextMatch < matches.size() ? nextMatch : -1;

					// after the match ends
					self.accept(string.substring(end), style);
				} else {
					// whole string is within the match

					texts.add(highlighted(string, style));
					counter.index += string.length();
				}
			} else if (start > 0 && start < string.length()) {
				// the match starts in this string

				// substring up until the match
				String substring = string.substring(0, start);

				texts.add(literal(substring, style));
				counter.index += substring.length();

				// the match onwards
				self.accept(string.substring(start), style);
			} else {
				// match is not contained within this string

				texts.add(literal(string, style));
				counter.index += string.length();
			}
		});

		target.visit((legacyStyle, legacyString) -> parseLegacyText(legacyString).visit((style, string) -> {
			addText.accept(string, style);

			return Optional.empty();
		}, legacyStyle), Style.EMPTY);

		Style style = target instanceof Text text ? text.getStyle() : Style.EMPTY;
		return new MutableText(TextComponent.EMPTY, texts, style);
	}

	static MutableText highlighted(String text, Style style) {
		return Text.literal(text).setStyle(highlight(style));
	}

	static MutableText literal(String text, Style style) {
		return Text.literal(text).setStyle(style);
	}

	private static final BiFunctionTempCache<String, String, List<Match>> MATCHES_CACHE = BiFunctionTempCache.create();
	private static final BiFunctionTempCache<StringVisitable, String, List<Match>> VISITABLE_MATCHES_CACHE = BiFunctionTempCache.create();

	private static final BiFunctionTempCache<String, String, Boolean> HAS_MATCHES_CACHE = BiFunctionTempCache.create();
	private static final BiFunctionTempCache<StringVisitable, String, Boolean> VISITABLE_HAS_MATCHES_CACHE = BiFunctionTempCache.create();

	private static final BiFunctionTempCache<StringVisitable, List<Match>, StringVisitable> HIGHLIGHT_CACHE = BiFunctionTempCache.create();
}
