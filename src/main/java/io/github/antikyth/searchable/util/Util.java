package io.github.antikyth.searchable.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.component.TextComponent;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

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
