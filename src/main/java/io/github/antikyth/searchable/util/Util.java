package io.github.antikyth.searchable.util;

import io.github.antikyth.searchable.util.function.Recursive;
import io.github.antikyth.searchable.util.match.Match;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.component.TextComponent;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class Util {
	/**
	 * The length of a formatting code within legacy text formatted using them.
	 */
	private static final int FORMATTING_CODE_LENGTH = 2;

	/**
	 * Formats the given {@code hint} as {@link Formatting#DARK_GRAY}.
	 */
	public static MutableText hint(MutableText hint) {
		return hint.formatted(Formatting.DARK_GRAY);
	}

	public static MutableText technicalName(MutableText technicalName) {
		return technicalName.formatted(Formatting.GRAY, Formatting.ITALIC);
	}

	static Style highlight(Style style) {
		return style.withFormatting(Formatting.UNDERLINE, Formatting.WHITE);
	}

	public static StringVisitable highlightMatches(StringVisitable target, List<Match> matches) {
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

	/**
	 * Parses legacy text formatted using legacy formatting codes using {@code §} into {@link Text}.
	 * <p>
	 * This returns {@link this.parseLegacyText parseLegacyText}{@code (legacy)}.
	 *
	 * @param legacy The legacy text to be parsed.
	 */
	public static Text parseLegacyText(final String legacy) {
		return parseLegacyText(legacy, '§');
	}

	/**
	 * Parses legacy  text formatted using legacy formatting codes into {@link Text}.
	 *
	 * @param legacy The legacy text to be parsed.
	 * @param code   The character used for color codes (i.e. {@code §} or {@code &}).
	 */
	public static Text parseLegacyText(final String legacy, final char code) {
		// If the text is not long enough to have formatting codes, return immediately.
		if (legacy.length() <= FORMATTING_CODE_LENGTH) return Text.literal(legacy);

		Style style = Style.EMPTY;

		int index = 0;
		int nextCodeIndex = legacy.indexOf(code, index);

		if (nextCodeIndex < 0) {
			// If there are no formatting codes found, return immediately.
			return Text.literal(legacy);
		} else {
			List<Text> texts = new ArrayList<>();

			do {
				// If there is text between the previous formatting code and the next one, add it.
				if (nextCodeIndex > index) {
					String string = legacy.substring(index, nextCodeIndex);
					texts.add(Text.literal(string).setStyle(style));
				}

				var formatting = Formatting.byCode(legacy.charAt(nextCodeIndex + 1));
				if (formatting != null) {
					// If it was a valid formatting code, apply it and start the next text after it.
					style = style.withFormatting(Formatting.byCode(legacy.charAt(nextCodeIndex + 1)));
					index = nextCodeIndex + FORMATTING_CODE_LENGTH;
				} else {
					// If it was not a valid formatting code, then don't apply it and include it in the next text.
					index = nextCodeIndex;
				}

				// Find the next formatting code.
				nextCodeIndex = legacy.indexOf(code, index);
			} while (index < legacy.length() && nextCodeIndex >= 0);

			// Add the last section of text if it wasn't already added.
			if (index < legacy.length()) {
				String string = legacy.substring(index);
				texts.add(Text.literal(string).setStyle(style));
			}

			return new MutableText(TextComponent.EMPTY, texts, Style.EMPTY);
		}
	}
}
