package io.github.antikyth.searchable.util;

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

public class Util {
	/**
	 * The length of a formatting code within legacy text formatted using them.
	 */
	private static final int FORMATTING_CODE_LENGTH = 2;

	private static Style highlight(Style style) {
		return style.withFormatting(Formatting.UNDERLINE, Formatting.WHITE);
	}

	/**
	 * Formats the given {@code hint} as {@link Formatting#DARK_GRAY}.
	 */
	public static MutableText hint(MutableText hint) {
		return hint.formatted(Formatting.DARK_GRAY);
	}

	/**
	 * Returns the text with a highlight if the {@code query} is found within the {@code text}, or the original
	 * {@code text} otherwise.
	 */
	public static StringVisitable textWithHighlight(String query, StringVisitable visitable) {
		if (query == null || query.isEmpty() || visitable == null) return visitable;

		var stripped = Formatting.strip(visitable.getString());
		if (stripped == null) return visitable;

		// Index of the beginning of the query match.
		int startIndex = stripped.toLowerCase(Locale.ROOT).indexOf(query.toLowerCase(Locale.ROOT));
		int endIndex = startIndex + query.length();

		if (startIndex < 0) {
			// If there was no match, return the text.
			return visitable;
		} else {
			List<Text> texts = new ArrayList<>();
			// Non-final variables cannot be used within the lambda below, and since to change an int you have to
			// re-assign it, we have to wrap it in a `new Object()`.
			var counter = new Object() {
				int index = 0;
			};
			visitable.visit((outerStyle, outerString) -> parseLegacyText(outerString).visit((style, string) -> {
				// start, relative to this string
				var start = startIndex - counter.index;
				// end, relative to this string
				var end = endIndex - counter.index;

				if (start <= 0 && end > 0) {
					// query match starts immediately

					if (end < string.length()) {
						// query ends within this string

						// query text
						var highlight = highlighted(string.substring(0, end), style);
						texts.add(highlight);
						// text after query
						var right = literal(string.substring(end), Style.EMPTY.withParent(style));
						texts.add(right);
					} else {
						var highlight = highlighted(string, style);
						texts.add(highlight);
					}
				} else if (start > 0 && start < string.length()) {
					// query starts within this string

					// text up until query
					var left = literal(string.substring(0, start), style);
					texts.add(left);

					if (end < string.length()) {
						// query ends within this string

						// query text
						var highlight = highlighted(string.substring(start, end), style);
						texts.add(highlight);
						// text after query
						var right = literal(string.substring(end), style);
						texts.add(right);
					} else {
						// query ends after this string

						// query text
						var highlight = highlighted(string.substring(start), style);
						texts.add(highlight);
					}
				} else {
					// query does not match within this string

					// non-query text
					texts.add(literal(string, style));
				}

				counter.index += string.length();

				return Optional.empty();
			}, outerStyle), Style.EMPTY);

			Style style = visitable instanceof Text text ? text.getStyle() : Style.EMPTY;
			return new MutableText(TextComponent.EMPTY, texts, style);
		}
	}

	private static MutableText highlighted(String text, Style style) {
		return Text.literal(text).setStyle(highlight(style));
	}

	private static MutableText literal(String text, Style style) {
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
