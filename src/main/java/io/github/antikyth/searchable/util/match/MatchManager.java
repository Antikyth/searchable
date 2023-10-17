/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.util.match;

import io.github.antikyth.searchable.config.SearchableConfig;
import io.github.antikyth.searchable.util.Util;
import io.github.antikyth.searchable.util.function.BiFunctionTempCache;
import io.github.antikyth.searchable.util.function.MatcherTriFunctionTempCache;
import io.github.antikyth.searchable.util.function.TriFunctionTempCache;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.stream.Collectors;

public class MatchManager {
	/**
	 * Wraps the {@linkplain MatchManager#matcher() global matcher} with caches and support for {@link StringVisitable}s
	 * and highlighting.
	 * <p>
	 * An instance of this class keeps very temporary caches of the last result, as the same inputs tend to be used many
	 * times all in a row when it comes to search queries and highlighting their matches.
	 */
	public MatchManager() {
	}

	/**
	 * Gets all the {@linkplain Match matches} for the given {@code query} within the given {@code target} text.
	 *
	 * @return A list of non-overlapping {@linkplain Match matches} in ascending order of indexes.
	 * @see MatchManager#getMatches(String, String)
	 */
	public List<Match> getMatches(StringVisitable target, String query) {
		return visitableGetMatchesCache.apply(matcher(), target, query, (matcher, _target, _query) -> {
			var stripped = Formatting.strip(_target.getString());
			if (stripped == null) return null;

			return getMatches(stripped, _query);
		});
	}

	/**
	 * Gets all the {@linkplain Match matches} for the given {@code query} within the given {@code target} string.
	 *
	 * @return A list of non-overlapping {@linkplain Match matches} in ascending order of indexes.
	 * @see MatchManager#getMatches(StringVisitable, String)
	 */
	public List<Match> getMatches(String target, String query) {
		return getMatchesCache.apply(matcher(), target, query, Matcher::findMatches);
	}

	/**
	 * Returns whether there are any matches for the given {@code query} within the given {@code target} text.
	 *
	 * @see MatchManager#hasMatches(String, String)
	 */
	public boolean hasMatches(StringVisitable target, String query) {
		return visitableHasMatchesCache.apply(matcher(), target, query, (matcher, _target, _query) -> {
			if (_target == null) return true;

			var stripped = Formatting.strip(_target.getString());
			assert (stripped != null);

			return hasMatches(stripped, _query);
		});
	}

	/**
	 * Returns whether there are any matches for the given {@code query} within the given {@code target} string.
	 *
	 * @see MatchManager#hasMatches(StringVisitable, String)
	 */
	public boolean hasMatches(String target, String query) {
		return hasMatchesCache.apply(matcher(), target, query, Matcher::hasMatches);
	}

	/**
	 * Highlights the {@linkplain Match matches} for the given {@code query} within the text created from the given
	 * {@code string}.
	 *
	 * @param string The string from which the text to be highlighted is created.
	 * @param query  The query for which {@linkplain Match matches} are found using
	 * @return The newly highlighted text.
	 * @see MatchManager#getHighlightedText(StringVisitable, String)
	 * @see MatchManager#getHighlightedText(String, List)
	 * @see MatchManager#getHighlightedText(StringVisitable, List)
	 */
	public StringVisitable getHighlightedText(String string, String query) {
		return stringHighlightCache.apply(string, query, (_string, _query) -> this.getHighlightedText(Text.literal(_string), _query));
	}

	/**
	 * Highlights the {@linkplain Match matches} for the given {@code query} within the given {@code text}.
	 *
	 * @param text  The text which is to be highlighted.
	 * @param query The query for which {@linkplain Match matches} are found using
	 * @return The newly highlighted text.
	 * @see MatchManager#getHighlightedText(String, String)
	 * @see MatchManager#getHighlightedText(String, List)
	 * @see MatchManager#getHighlightedText(StringVisitable, List)
	 */
	public StringVisitable getHighlightedText(StringVisitable text, String query) {
		return highlightCache.apply(text, query, (_text, _query) -> this.getHighlightedText(_text, this.getMatches(_text, _query)));
	}

	/**
	 * Highlights the given {@code matches} within the text created from the given {@code string}.
	 *
	 * @param string  The string from which the text to be highlighted is created.
	 * @param matches The matches which are to be highlighted.
	 * @return The newly highlighted text.
	 * @see MatchManager#getHighlightedText(String, String)
	 * @see MatchManager#getHighlightedText(StringVisitable, String)
	 * @see MatchManager#getHighlightedText(StringVisitable, List)
	 */
	public StringVisitable getHighlightedText(String string, List<Match> matches) {
		return stringMatchesHighlightCache.apply(string, matches, (_string, _matches) -> this.getHighlightedText(Text.literal(_string), _matches));
	}

	/**
	 * Highlights the given {@code matches} within the given {@code text}.
	 *
	 * @param text    The text which is to be highlighted.
	 * @param matches The matches which are to be highlighted.
	 * @return The newly highlighted text.
	 * @see MatchManager#getHighlightedText(String, String)
	 * @see MatchManager#getHighlightedText(StringVisitable, String)
	 * @see MatchManager#getHighlightedText(String, List)
	 */
	public StringVisitable getHighlightedText(StringVisitable text, List<Match> matches) {
		return matchesHighlightCache.apply(text, matches, Util::highlightMatches);
	}

	public OrderedText getHighlightedText(OrderedText target, StringVisitable text, String query) {
		return orderedTextStringVisitableHighlightCache.apply(target, text, query, (_target, _text, _query) -> this.getHighlightedText(_target, this.getMatches(_text, _query)));
	}

	public OrderedText getHighlightedText(OrderedText target, String text, String query) {
		return orderedTextStringHighlightCache.apply(target, text, query, (_target, _text, _query) -> this.getHighlightedText(_target, this.getMatches(_text, _query)));
	}

	public OrderedText getHighlightedText(OrderedText target, String query) {
		return this.orderedTextHighlightCache.apply(target, query, (_target, _query) -> {
			String text = Util.orderedTextToString(_target);

			return this.getHighlightedText(_target, text, _query);
		});
	}

	public OrderedText getHighlightedText(OrderedText target, List<Match> matches) {
		return orderedTextMatchesHighlightCache.apply(target, matches, Util::highlightOrderedText);
	}

	public List<OrderedText> getHighlightedOrderedTexts(List<OrderedText> texts, String query) {
		return orderedTextsHighlightCache.apply(texts, query, (orderedTexts, _query) -> orderedTexts.stream().map(orderedText -> {
			String string = Util.orderedTextToString(orderedText);

			return this.getHighlightedText(orderedText, string, _query);
		}).collect(Collectors.toList()));
	}

	// Matching caches {{{
	private final MatcherTriFunctionTempCache<String, String, List<Match>> getMatchesCache = MatcherTriFunctionTempCache.create();
	private final MatcherTriFunctionTempCache<StringVisitable, String, List<Match>> visitableGetMatchesCache = MatcherTriFunctionTempCache.create();

	private final MatcherTriFunctionTempCache<String, String, Boolean> hasMatchesCache = MatcherTriFunctionTempCache.create();
	private final MatcherTriFunctionTempCache<StringVisitable, String, Boolean> visitableHasMatchesCache = MatcherTriFunctionTempCache.create();
	// }}}

	// Highlight caches {{{
	private final BiFunctionTempCache<StringVisitable, String, StringVisitable> highlightCache = BiFunctionTempCache.create();
	private final BiFunctionTempCache<StringVisitable, List<Match>, StringVisitable> matchesHighlightCache = BiFunctionTempCache.create();

	private final BiFunctionTempCache<String, String, StringVisitable> stringHighlightCache = BiFunctionTempCache.create();
	private final BiFunctionTempCache<String, List<Match>, StringVisitable> stringMatchesHighlightCache = BiFunctionTempCache.create();

	private final TriFunctionTempCache<OrderedText, StringVisitable, String, OrderedText> orderedTextStringVisitableHighlightCache = TriFunctionTempCache.create();
	private final TriFunctionTempCache<OrderedText, String, String, OrderedText> orderedTextStringHighlightCache = TriFunctionTempCache.create();
	private final BiFunctionTempCache<OrderedText, List<Match>, OrderedText> orderedTextMatchesHighlightCache = BiFunctionTempCache.create();
	private final BiFunctionTempCache<OrderedText, String, OrderedText> orderedTextHighlightCache = BiFunctionTempCache.create();

	private final BiFunctionTempCache<List<OrderedText>, String, List<OrderedText>> orderedTextsHighlightCache = BiFunctionTempCache.create();
	// }}}

	/**
	 * Returns the {@link Matcher} currently in use by all {@link MatchManager}s.
	 */
	public static Matcher matcher() {
		return useRegexMatching() ? Matchers.REGEX : Matchers.PLAIN;
	}

	@Unique
	private static boolean useRegexMatching() {
		return SearchableConfig.INSTANCE != null && SearchableConfig.INSTANCE.use_regex_matching.value();
	}
}
